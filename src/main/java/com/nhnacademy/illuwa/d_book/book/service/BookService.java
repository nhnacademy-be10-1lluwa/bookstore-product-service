package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.common.aop.BookRegisterSync;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookCountUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookApiRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailWithExtraInfoResponse;
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
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.repository.BookTagRepository;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Service
@Slf4j
public class BookService {
    private final AladinBookApiService aladinBookApiService;
    private final BookRepository bookRepository;
    private final BookResponseMapper bookResponseMapper;
    private final BookTagRepository bookTagRepository;
    private final BookImageRepository bookImageRepository;
    private final BookMapper bookMapper;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final MinioStorageService minioStorageService;
    private final BookSearchService bookSearchService;


    public BookService(AladinBookApiService aladinBookApiService, BookRepository bookRepository, BookResponseMapper bookResponseMapper, TagRepository tagRepository, BookTagRepository bookTagRepository, BookImageRepository bookImageRepository, BookMapper bookMapper, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository, MinioStorageService minioStorageService, BookSearchService bookSearchService) {
        this.aladinBookApiService = aladinBookApiService;
        this.bookRepository = bookRepository;
        this.bookResponseMapper = bookResponseMapper;
        this.bookTagRepository = bookTagRepository;
        this.bookImageRepository = bookImageRepository;
        this.bookMapper = bookMapper;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.minioStorageService = minioStorageService;
        this.bookSearchService = bookSearchService;
    }

    // 외부 API에서 도서 검색
    public List<BookExternalResponse> searchBookFromExternalApi(String title) {
        List<BookExternalResponse> bookExternalResponseDtos = aladinBookApiService.searchBooksByTitle(title);

        if (bookExternalResponseDtos == null || bookExternalResponseDtos.isEmpty()) {
            throw new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다.");
        }
        return bookExternalResponseDtos;
    }


    // 도서 ID로 상세 조회
    @Transactional(readOnly = true)
    public BookDetailResponse searchBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            throw new NotFoundBookException("해당 ID(" + id + ")의 도서가 존재하지 않습니다.");
        }

        Book bookEntity = book.get();
        log.info("조회된 도서의 id : {}", bookEntity.getId());

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }

    @Transactional(readOnly = true)
    public Page<BookDetailResponse> getAllBooksWithDetails(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        return bookPage.map(bookResponseMapper::toBookDetailResponse);
    }


    // ISBN으로 도서 상세 조회
    @Transactional(readOnly = true)
    public BookDetailResponse searchBookByIsbn(String isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);

        if (book.isEmpty()) {
            throw new NotFoundBookException("해당 isbn(" + isbn + ")의 도서가 존재하지 않습니다.");
        }

        Book bookEntity = book.get();

        log.info("조회된 도서의 isbn : {}", bookEntity.getIsbn());

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }


    // 제목으로 도서 목록 검색
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


    // 도서 상세 (부가정보 포함) 조회
    @Transactional
    public BookDetailWithExtraInfoResponse getBookDetailWithExtraInfo(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("도서를 찾을 수 없습니다."));

        Long categoryId = null;
        Long level1 = null;
        Long level2 = null;

        if (book.getBookCategories() != null && !book.getBookCategories().isEmpty()) {
            BookCategory bookCategory = book.getBookCategories().iterator().next();
            Category category = bookCategory.getCategory(); // 추가 쿼리 발생 안 함

            if (category != null) {
                categoryId = category.getId();
                if (category.getParentCategory() != null) {
                    level2 = category.getParentCategory().getId();
                    if (category.getParentCategory().getParentCategory() != null) {
                        level1 = category.getParentCategory().getParentCategory().getId();
                    }
                }
            }
        }

        BookDetailWithExtraInfoResponse response = BookDetailWithExtraInfoResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .contents(book.getContents())
                .description(book.getDescription())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate().toString())
                .isbn(book.getIsbn())
                .regularPrice(book.getRegularPrice())
                .salePrice(book.getSalePrice())
                .imgUrl(book.getBookImages().isEmpty() ? null : book.getBookImages().get(0).getImageUrl())
                .giftwrap(book.getBookExtraInfo().getGiftWrap())
                .count(book.getBookExtraInfo().getCount())
                .status(book.getBookExtraInfo().getStatus())
                .categoryId(categoryId)
                .level1(level1)
                .level2(level2)
                .build();


        return response;
    }


    // 모든 도서 조회 (부가정보 포함)
    @Transactional(readOnly = true)
    public Page<BookDetailWithExtraInfoResponse> getAllBooksWithExtraInfo(Pageable pageable) {
        Page<Book> all = bookRepository.findAll(pageable);
        return all
                .map(book -> {
                    // 카테고리 조회
                    BookCategory bookCategory = book.getBookCategories().stream().findFirst().orElse(null);

                    // 태그 조회
                    List<TagResponse> tags = book.getBookTags().stream()
                            .map(bookTag -> new TagResponse(bookTag.getTag().getId(), bookTag.getTag().getName()))
                            .toList();

                    return new BookDetailWithExtraInfoResponse().toDto(book, bookCategory, tags);
                });
    }


    // 도서 페이징 조회
    public Page<BookDetailResponse> getAllBooksByPaging(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);

        Page<BookDetailResponse> pageMap = bookPage.map(bookResponseMapper::toBookDetailResponse);

        return pageMap;
    }

//
//    // 카테고리+태그로 도서 검색 (페이징)
//    @Transactional(readOnly = true)
//    public Page<BookDetailResponse> searchBooksByCriteria(Long categoryId, String tagName, Pageable pageable) {
//        Page<Book> bookPage = bookRepository.findBooksByCriteria(categoryId, tagName, pageable);
//
//        return bookPage.map(bookResponseMapper::toBookDetailResponse);
//    }
//

    // 모든 도서 조회 (간단 정보)
    @Transactional(readOnly = true)
    public List<BookDetailResponse> getAllBooks() {
        List<Book> bookEntityList = bookRepository.findAll();
        return bookResponseMapper.toBookDetailListResponse(bookEntityList);
    }

    // 외부 API 데이터로 도서 등록
    @Transactional
    @BookRegisterSync
    public BookDetailResponse registerBookByApi(BookApiRegisterRequest bookApiRegisterRequest) {

        Book bookEntity = bookMapper.fromApiRequest(bookApiRegisterRequest);

        if (bookEntity == null) {
            throw new IllegalArgumentException("등록할 도서가 존재하지 않습니다.");
        }

        bookEntity.setBookImages(new ArrayList<>());

        if (bookRepository.existsByIsbn(bookEntity.getIsbn())) {
            log.warn("이미 등록된 도서: 제목={}", bookEntity.getTitle());
            throw new BookAlreadyExistsException("이미 등록된 도서입니다.");
        }

        Category categoryEntity = categoryRepository.findById(bookApiRegisterRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        log.info("도서 등록 시작: 제목={}", bookEntity.getTitle());

        BookImage bookImage = new BookImage(bookEntity, bookApiRegisterRequest.getCover(), ImageType.THUMBNAIL);
        bookEntity.addImage(bookImage);

        BookExtraInfo bookExtraInfo = new BookExtraInfo(Status.NORMAL, true, bookApiRegisterRequest.getCount());
        bookEntity.setBookExtraInfo(bookExtraInfo);


        Book savedBook = bookRepository.save(bookEntity);
        bookImageRepository.save(bookImage);

        BookCategory bookCategory = new BookCategory(savedBook, categoryEntity);
        savedBook.getBookCategories().add(bookCategory);
        bookCategoryRepository.save(bookCategory);

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }

    // 도서 직접 등록 (폼+파일 업로드)
    @BookRegisterSync
    public BookDetailResponse registgerBookDirectly(BookRegisterRequest bookRegisterRequest, MultipartFile bookImageFile) {

        String savedImageName = minioStorageService.uploadBookImage(bookImageFile);

        bookRegisterRequest.getParsedPubDate();

        Book bookEntity = bookMapper.toBookEntity(bookRegisterRequest);

        bookEntity.setBookImages(new ArrayList<>());

        log.info("도서 등록 시작: 제목={}", bookEntity.getTitle());

        if (bookRepository.existsByIsbn(bookEntity.getIsbn())) {
            log.warn("이미 등록된 도서: 제목={}", bookEntity.getTitle());
            throw new BookAlreadyExistsException("이미 등록된 도서입니다.");
        }

        // 카테고리
        Category categoryEntity = categoryRepository.findById(bookRegisterRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        //이미지 MiniO 등록 예정
        BookImage bookImage = new BookImage(bookEntity, savedImageName, ImageType.THUMBNAIL);
        bookEntity.addImage(bookImage);

        //도서 부가 정보
        BookExtraInfo bookExtraInfo = new BookExtraInfo(Status.NORMAL, true, bookRegisterRequest.getCount());
        bookEntity.setBookExtraInfo(bookExtraInfo);


        Book savedBook = bookRepository.save(bookEntity);
        bookImageRepository.save(bookImage);
        bookCategoryRepository.save(new BookCategory(savedBook, categoryEntity));


        return bookResponseMapper.toBookDetailResponse(bookEntity); // Entity -> DTO
    }


    // 도서 삭제(T)
    @Transactional
    public void deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            log.warn("도서를 찾을 수 없습니다. id : {}", id);
            throw new NotFoundBookException("id : " + id + "에 해당하는 도서를 찾을 수 없습니다.");
        }

        bookSearchService.deleteById(id);

        Book targetBook = book.get();
        bookRepository.delete(targetBook);

        log.info("삭제된 도서 제목 : {}", targetBook.getTitle());
    }


    // 도서 수정
    @Transactional
    public void updateBook(Long id, BookUpdateRequest requestDto) {
        Optional<Book> bookById = bookRepository.findById(id);
        if (bookById.isEmpty()) {
            throw new NotFoundBookException("해당 ID(" + id + ")의 도서가 존재하지 않습니다.");
        }

        Book book = bookById.get();

        if (requestDto.getTitle() != null) {
            book.setTitle(requestDto.getTitle());
        }
        if (requestDto.getAuthor() != null) {
            book.setAuthor(requestDto.getAuthor());
        }
        if (requestDto.getPublisher() != null) {
            book.setPublisher(requestDto.getPublisher());
        }
        if (requestDto.getDescription() != null) {
            book.setDescription(requestDto.getDescription());
        }
        if (requestDto.getContents() != null) {
            book.setContents(requestDto.getContents());
        }
        if (requestDto.getRegularPrice() != null) {
            book.setRegularPrice(requestDto.getRegularPrice());
        }
        if (requestDto.getSalePrice() != null) {
            book.setSalePrice(requestDto.getSalePrice());
        }

        if (requestDto.getCount() != null) {
            book.getBookExtraInfo().setCount(requestDto.getCount());
        }
        if (requestDto.getGiftwrap() != null) {
            book.getBookExtraInfo().setGiftWrap(requestDto.getGiftwrap());
        }
        if (requestDto.getStatus() != null) {
            book.getBookExtraInfo().setStatus(Status.valueOf(requestDto.getStatus()));
        }

        if (requestDto.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

            // 기존 BookCategory 찾아서 교체
            BookCategory bookCategory = bookCategoryRepository.findByBookId(book.getId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리 정보가 없습니다."));
            bookCategory.setCategory(newCategory);
        }

        if (requestDto.getCover() != null && !requestDto.getCover().isBlank()) {
            BookImage bookImage = bookImageRepository.findByBookIdAndImageType(book.getId(), ImageType.THUMBNAIL)
                    .orElseThrow(() -> new IllegalArgumentException("대표 이미지 정보가 없습니다."));

            bookImage.setImageUrl(requestDto.getCover());
        }

        bookSearchService.syncBookToElasticsearch(book);


    }

    @Transactional(readOnly = true)
    public List<Book> findBooksByCategoryAndSubCategories(String categoryName) {

        Category parentCategory = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + categoryName));


        List<Long> allCategoryIds = new ArrayList<>();
        Queue<Category> categoriesToVisit = new LinkedList<>();

        categoriesToVisit.add(parentCategory);

        while (!categoriesToVisit.isEmpty()) {

            Category currentCategory = categoriesToVisit.poll();
            allCategoryIds.add(currentCategory.getId());
            if (currentCategory.getChildrenCategory() != null) {
                categoriesToVisit.addAll(currentCategory.getChildrenCategory());
            }
        }

        return bookRepository.findBooksByCategories(allCategoryIds);
    }

    @Transactional
    public void updateBooksCount(List<BookCountUpdateRequest> requests, String sign) {

        boolean isIncrease = "positive".equalsIgnoreCase(sign);

        for (BookCountUpdateRequest request : requests) {

            Book book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new NotFoundBookException("ID " + request.getBookId() + "에 해당하는 도서를 찾을 수 없습니다."));

            if (book.getBookExtraInfo() == null) {
                throw new IllegalStateException("도서에 부가 정보가 존재하지 않습니다.");
            }

            int currentCount = Optional.ofNullable(book.getBookExtraInfo().getCount()).orElse(0);
            int changeAmount = isIncrease ? Math.abs(request.getBookCount()) : -Math.abs(request.getBookCount());
            int updatedCount = currentCount + changeAmount;

            if (updatedCount < 0) {
                throw new IllegalArgumentException("재고 부족: 현재 재고=" + currentCount + ", 요청 차감=" + request.getBookCount());
            }

            book.getBookExtraInfo().setCount(updatedCount);
            bookRepository.save(book);
        }
    }
}

