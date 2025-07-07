package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookImageRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookService {
    private final AladinBookApiService aladinBookApiService;
    private final BookRepository bookRepository;
    private final BookResponseMapper bookResponseMapper;
    private final TagRepository tagRepository;
    private final BookImageRepository bookImageRepository;
    private final BookMapper bookMapper;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final MinioStorageService minioStorageService;


    public BookService(AladinBookApiService aladinBookApiService, BookRepository bookRepository, BookResponseMapper bookResponseMapper, TagRepository tagRepository, BookImageRepository bookImageRepository, BookMapper bookMapper, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository, MinioStorageService minioStorageService) {
        this.aladinBookApiService = aladinBookApiService;
        this.bookRepository = bookRepository;
        this.bookResponseMapper = bookResponseMapper;
        this.tagRepository = tagRepository;
        this.bookImageRepository = bookImageRepository;
        this.bookMapper = bookMapper;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.minioStorageService = minioStorageService;
    }

    //도서 등록 전 도서 검색
    public List<BookExternalResponse> searchBookFromExternalApi(String title) {
        List<BookExternalResponse> bookExternalResponseDtos = aladinBookApiService.searchBooksByTitle(title);

        if (bookExternalResponseDtos == null || bookExternalResponseDtos.isEmpty()) {
            throw new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다.");
        }
            return bookExternalResponseDtos;
    }


    @Transactional(readOnly = true)
    public BookDetailResponse searchBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            throw new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다.");
        }

        Book bookEntity = book.get();
        log.info("조회된 도서의 id : {}", bookEntity.getId());

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }

    //도서 수정 전 도서 검색
    @Transactional(readOnly = true)
    public List<BookDetailResponse> searchBookByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContaining(title);

        if (books.isEmpty()) {
            throw new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다.");
        }

        List<BookDetailResponse> bookDetailList = books.stream().map(bookResponseMapper::toBookDetailResponse).toList();
        log.info("검색된 도서의 수 : {}", bookDetailList.size());

        return bookDetailList;
    }

    @Transactional
    public BookDetailResponse registerBook(BookRegisterRequest bookRegisterRequest) {

        Book bookEntity = bookMapper.toBookEntity(bookRegisterRequest);
        bookEntity.setBookImages(new ArrayList<>());

        Category categoryEntity = categoryRepository.findById(bookRegisterRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));


        if (bookEntity == null) {
            throw new IllegalArgumentException("등록할 도서가 존재하지 않습니다.");
        }

        log.info("도서 등록 시작: 제목={}", bookEntity.getTitle());
        if (bookRepository.existsByIsbn(bookEntity.getIsbn())) {
            log.warn("이미 등록된 도서: 제목={}", bookEntity.getTitle());
            throw new BookAlreadyExistsException("이미 등록된 도서입니다.");
        }

        BookImage bookImage = new BookImage(bookEntity,bookRegisterRequest.getImgUrl(), ImageType.THUMBNAIL);
        bookEntity.addImage(bookImage);

        BookExtraInfo bookExtraInfo = new BookExtraInfo(Status.NORMAL,true, bookRegisterRequest.getCount());
        bookEntity.setBookExtraInfo(bookExtraInfo);

        bookCategoryRepository.save(new BookCategory(bookEntity,categoryEntity));

        bookRepository.save(bookEntity);

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }

    @Transactional(readOnly = true)
    public List<BookDetailResponse> getAllBooks(){
        List<Book> bookEntityList = bookRepository.findAll();
        return bookResponseMapper.toBookDetailListResponse(bookEntityList);
    }


    @Transactional
    public void updateBook(Long id, BookUpdateRequest requestDto) {
        Optional<Book> bookById = bookRepository.findById(id);

        if(bookById.isEmpty()){
            log.info("해당 도서를 찾을 수 없습니다. id : {}", id);
            throw new NotFoundBookException("해당 도서는 존재하지 않아서 수정이 불가능합니다.");
        }

        Book book = bookById.get();
        String description = requestDto.getDescription();
        String contents = requestDto.getContents();
        Integer price = requestDto.getPrice();

        if(description != null){
            book.setDescription(description);
        }
        if(contents != null) {
            book.setContents(contents);
        }
        if(price != null){
            book.setRegularPrice(price);
        }
    }

    @Transactional
    public void deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if(book.isEmpty()){
            log.warn("도서를 찾을 수 없습니다. id : {}",id);
            throw new NotFoundBookException("id : " + id + "에 해당하는 도서를 찾을 수 없습니다.");
        }

        Book targetBook = book.get();
        bookRepository.delete(targetBook);

        log.info("삭제된 도서 제목 : {}" , targetBook.getTitle());
    }


    public Page<BookDetailResponse> getAllBooksByPaging(Pageable pageable){
        Page<Book> bookPage = bookRepository.findAll(pageable);

        Page<BookDetailResponse> pageMap = bookPage.map(bookResponseMapper::toBookDetailResponse);

        return pageMap;
    }


    public BookDetailResponse createBookDirectly(BookRegisterRequest bookRegisterRequest, MultipartFile bookImageFile) {
        String savedImageName = minioStorageService.uploadBookImage(bookImageFile);

        Book bookEntity = bookMapper.toBookEntity(bookRegisterRequest);
        bookEntity.setBookImages(new ArrayList<>());

        Category categoryEntity = categoryRepository.findById(bookRegisterRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        if (bookEntity == null) {
            throw new IllegalArgumentException("등록할 도서가 존재하지 않습니다.");
        }

        log.info("도서 등록 시작: 제목={}", bookEntity.getTitle());
        if (bookRepository.existsByIsbn(bookEntity.getIsbn())) {
            log.warn("이미 등록된 도서: 제목={}", bookEntity.getTitle());
            throw new BookAlreadyExistsException("이미 등록된 도서입니다.");
        }

        BookImage bookImage = new BookImage(bookEntity,savedImageName, ImageType.THUMBNAIL);
        bookEntity.addImage(bookImage);

        BookExtraInfo bookExtraInfo = new BookExtraInfo(Status.NORMAL,true, bookRegisterRequest.getCount());
        bookEntity.setBookExtraInfo(bookExtraInfo);

        bookCategoryRepository.save(new BookCategory(bookEntity,categoryEntity));

        bookRepository.save(bookEntity);


        return bookResponseMapper.toBookDetailResponse(bookEntity); // Entity -> DTO
    }

    @Transactional(readOnly = true)
    public Page<BookDetailResponse> searchBooksByCriteria(Long categoryId, String tagName, Pageable pageable) {
        Page<Book> bookPage = bookRepository.findBooksByCriteria(categoryId, tagName, pageable);

        return bookPage.map(bookResponseMapper::toBookDetailResponse);
    }

}
