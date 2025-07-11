package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.request.BookApiRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.FinalAladinBookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import com.nhnacademy.illuwa.d_book.book.document.BookDocument;
import com.nhnacademy.illuwa.d_book.book.repository.BookSearchRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final BookSearchRepository bookSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    public BookService(AladinBookApiService aladinBookApiService, BookRepository bookRepository, BookResponseMapper bookResponseMapper, TagRepository tagRepository, BookImageRepository bookImageRepository, BookMapper bookMapper, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository, MinioStorageService minioStorageService, BookSearchRepository bookSearchRepository, ElasticsearchOperations elasticsearchOperations) {
        this.aladinBookApiService = aladinBookApiService;
        this.bookRepository = bookRepository;
        this.bookResponseMapper = bookResponseMapper;
        this.tagRepository = tagRepository;
        this.bookImageRepository = bookImageRepository;
        this.bookMapper = bookMapper;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.minioStorageService = minioStorageService;
        this.bookSearchRepository = bookSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
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

    @Transactional(readOnly = true)
    public BookDetailResponse searchBookByIsbn(String isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);

        if (book.isEmpty()) {
            throw new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다.");
        }

        Book bookEntity = book.get();

        log.info("조회된 도서의 isbn : {}", bookEntity.getIsbn());

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

        BookImage bookImage = new BookImage(bookEntity,bookRegisterRequest.getImageFile().getName(), ImageType.THUMBNAIL);
        bookEntity.addImage(bookImage);

        BookExtraInfo bookExtraInfo = new BookExtraInfo(Status.NORMAL,true, bookRegisterRequest.getCount());
        bookEntity.setBookExtraInfo(bookExtraInfo);

        bookCategoryRepository.save(new BookCategory(bookEntity,categoryEntity));

        Book savedBook = bookRepository.save(bookEntity);
        syncBookToElasticsearch(savedBook);

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }

    @Transactional
    public BookDetailResponse registerBookByApi(BookApiRegisterRequest bookApiRegisterRequest) {

        Book bookEntity = bookMapper.fromApiRequest(bookApiRegisterRequest);
        bookEntity.setBookImages(new ArrayList<>());

        Category categoryEntity = categoryRepository.findById(bookApiRegisterRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));


        if (bookEntity == null) {
            throw new IllegalArgumentException("등록할 도서가 존재하지 않습니다.");
        }

        log.info("도서 등록 시작: 제목={}", bookEntity.getTitle());
        if (bookRepository.existsByIsbn(bookEntity.getIsbn())) {
            log.warn("이미 등록된 도서: 제목={}", bookEntity.getTitle());
            throw new BookAlreadyExistsException("이미 등록된 도서입니다.");
        }

        BookImage bookImage = new BookImage(bookEntity,bookApiRegisterRequest.getCover(), ImageType.THUMBNAIL);
        bookEntity.addImage(bookImage);

        BookExtraInfo bookExtraInfo = new BookExtraInfo(Status.NORMAL,true, bookApiRegisterRequest.getCount());
        bookEntity.setBookExtraInfo(bookExtraInfo);

        bookCategoryRepository.save(new BookCategory(bookEntity,categoryEntity));

        Book savedBook = bookRepository.save(bookEntity);
        syncBookToElasticsearch(savedBook);

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }

    @Transactional
    public BookDetailResponse registerBookByAladin(FinalAladinBookRegisterRequest bookRegisterRequest) {

        Book bookEntity = bookMapper.fromFinalAladinRequest(bookRegisterRequest);

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

        BookImage bookImage = new BookImage(bookEntity,bookRegisterRequest.getImageFileUrl(), ImageType.THUMBNAIL);

        bookEntity.addImage(bookImage);

        BookExtraInfo bookExtraInfo = new BookExtraInfo(Status.NORMAL,true, bookRegisterRequest.getCount());
        bookEntity.setBookExtraInfo(bookExtraInfo);

        bookCategoryRepository.save(new BookCategory(bookEntity,categoryEntity));

        Book savedBook = bookRepository.save(bookEntity);
        syncBookToElasticsearch(savedBook);

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
            book.setRegularPrice(BigDecimal.valueOf(requestDto.getPrice()));
        }
    }

    @Transactional
    public void deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if(book.isEmpty()){
            log.warn("도서를 찾을 수 없습니다. id : {}",id);
            throw new NotFoundBookException("id : " + id + "에 해당하는 도서를 찾을 수 없습니다.");
        }

        bookSearchRepository.deleteById(id);

        Book targetBook = book.get();
        bookRepository.delete(targetBook);


        log.info("삭제된 도서 제목 : {}" , targetBook.getTitle());
    }


    public Page<BookDetailResponse> getAllBooksByPaging(Pageable pageable){
        Page<Book> bookPage = bookRepository.findAll(pageable);

        Page<BookDetailResponse> pageMap = bookPage.map(bookResponseMapper::toBookDetailResponse);

        return pageMap;
    }


    public BookDetailResponse registgerBookDirectly(BookRegisterRequest bookRegisterRequest, MultipartFile bookImageFile) {

        String savedImageName = minioStorageService.uploadBookImage(bookImageFile);

        bookRegisterRequest.getParsedPubDate();

        Book bookEntity = bookMapper.toBookEntity(bookRegisterRequest);

        bookEntity.setBookImages(new ArrayList<>());

        //카테고리
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

        //이미지 MiniO 등록 예정
        BookImage bookImage = new BookImage(bookEntity,savedImageName, ImageType.THUMBNAIL);
        bookEntity.addImage(bookImage);

        //도서 부가 정보
        BookExtraInfo bookExtraInfo = new BookExtraInfo(Status.NORMAL,true, bookRegisterRequest.getCount());
        bookEntity.setBookExtraInfo(bookExtraInfo);



        Book savedBook = bookRepository.save(bookEntity);

        bookCategoryRepository.save(new BookCategory(savedBook,categoryEntity));

        syncBookToElasticsearch(savedBook);


        return bookResponseMapper.toBookDetailResponse(bookEntity); // Entity -> DTO
    }

    @Transactional(readOnly = true)
    public Page<BookDetailResponse> searchBooksByCriteria(Long categoryId, String tagName, Pageable pageable) {
        Page<Book> bookPage = bookRepository.findBooksByCriteria(categoryId, tagName, pageable);

        return bookPage.map(bookResponseMapper::toBookDetailResponse);
    }

    private void syncBookToElasticsearch(Book book) {
        BookDocument bookDocument = BookDocument.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .salePrice(book.getSalePrice())
                .thumbnailUrl(book.getBookImages() != null && !book.getBookImages().isEmpty() ? book.getBookImages().get(0).getImageUrl() : null)
                .build();
        bookSearchRepository.save(bookDocument);
    }

    @Transactional(readOnly = true)
    public Page<BookDocument> searchByKeyword(String keyword, Pageable pageable) {

        String[] searchFields = {"title", "description", "author"};

        Query query = new StringQuery(
                String.format("{\"multi_match\": {\"query\": \"%s\", \"fields\": [\"%s\"]}}",
                        keyword,
                        String.join("\", \"", searchFields))
        );
        query.setPageable(pageable);

        SearchHits<BookDocument> searchHits = elasticsearchOperations.search(query, BookDocument.class);

        List<BookDocument> content = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // 5. 최종 결과를 Page 객체로 포장해서 Controller에게 돌려줌
        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }


    @Transactional
    public void deleteBookAndRelatedEntities(Long bookId) {
        bookRepository.deleteBookAndRelatedEntities(bookId);
    }


}
