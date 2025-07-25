package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookApiRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookCountUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailWithExtraInfoResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookImageRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    AladinBookApiService aladinBookApiService;
    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Mock
    BookResponseMapper bookResponseMapper;

    @Mock
    BookImageRepository bookImageRepository;

    @Mock
    BookCategoryRepository bookCategoryRepository;


    @Mock
    CategoryRepository categoryRepository;

    @Mock
    MinioStorageService minioStorageService;


    @Mock
    BookSearchService bookSearchService;

    @InjectMocks
    BookService bookService;


    BookRegisterRequest bookRegisterRequest;

    @BeforeEach
    void setUp() {
        bookRegisterRequest = new BookRegisterRequest(
                "테스트 책 제목",
                "홍길동",
                "테스트 출판사",
                "테스트 내용",
                "2025-07-10",
                "1234567890123",
                20000,
                15000,
                "테스트 설명입니다.",
                new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "fake-image-content".getBytes()),
                10,
                1L
        );
    }

    @Test
    @DisplayName("도서 직접 등록 - 성공")
    void registerBookDirectly_Success() {
        // Given
        Book mockBook = Book.builder()
                .isbn(bookRegisterRequest.getIsbn())
                .bookExtraInfo(new BookExtraInfo(Status.NORMAL, true, bookRegisterRequest.getCount()))
                .bookImages(new ArrayList<>(Collections.singletonList(new BookImage(null, "http://test.url/image.jpg", ImageType.THUMBNAIL))))
                .build();
        when(bookRepository.existsByIsbn(bookRegisterRequest.getIsbn())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mock(Category.class)));
        when(bookMapper.toBookEntity(any(BookRegisterRequest.class))).thenReturn(mockBook);
        when(minioStorageService.uploadBookImage(any(MockMultipartFile.class))).thenReturn("http://test.url/image.jpg");
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);
        when(bookImageRepository.save(any(BookImage.class))).thenReturn(mock(BookImage.class));
        when(bookCategoryRepository.save(any(BookCategory.class))).thenReturn(mock(BookCategory.class));
        when(bookResponseMapper.toBookDetailResponse(any(Book.class))).thenReturn(mock(BookDetailResponse.class));

        // When
        bookService.registgerBookDirectly(bookRegisterRequest, bookRegisterRequest.getImageFile());

        // Then
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("도서 직접 등록 - 실패 (이미 존재하는 ISBN)")
    void registerBookDirectly_AlreadyExists_Failure() {
        // Given
        Book mockBook = Book.builder().isbn(bookRegisterRequest.getIsbn()).build();
        when(bookMapper.toBookEntity(any(BookRegisterRequest.class))).thenReturn(mockBook);
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> bookService.registgerBookDirectly(bookRegisterRequest, bookRegisterRequest.getImageFile()))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessageContaining("이미 등록된 도서입니다.");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("도서 직접 등록 - 실패 (카테고리 없음)")
    void registerBookDirectly_CategoryNotFound_Failure() {
        // Given
        Book mockBook = Book.builder().isbn(bookRegisterRequest.getIsbn()).build();
        when(bookMapper.toBookEntity(any(BookRegisterRequest.class))).thenReturn(mockBook);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.registgerBookDirectly(bookRegisterRequest, bookRegisterRequest.getImageFile()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("카테고리가 존재하지 않습니다.");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("API를 통한 도서 등록 - 성공")
    void registerBookByApi_Success() {
        // Given
        BookApiRegisterRequest apiRequest = new BookApiRegisterRequest(
                "API 테스트 책", "API 저자", "API 출판사", "API 내용", "2024-07-24", "9781234567890",
                25000, 20000, "API 설명", "http://api.cover.url", 50, 2L
        );
        Book book = Book.builder().id(1L).title("Test Book").isbn(apiRequest.getIsbn())
                .bookExtraInfo(new BookExtraInfo(Status.NORMAL, true, apiRequest.getCount()))
                .bookImages(new ArrayList<>(Collections.singletonList(new BookImage(null, apiRequest.getCover(), ImageType.THUMBNAIL))))
                .build();
        BookDetailResponse mockDetailResponse = mock(BookDetailResponse.class);

        when(bookRepository.existsByIsbn(apiRequest.getIsbn())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mock(Category.class)));
        when(bookMapper.fromApiRequest(any(BookApiRegisterRequest.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookImageRepository.save(any(BookImage.class))).thenReturn(mock(BookImage.class));
        when(bookCategoryRepository.save(any(BookCategory.class))).thenReturn(mock(BookCategory.class));
        when(bookResponseMapper.toBookDetailResponse(any(Book.class))).thenReturn(mockDetailResponse);

        // When
        BookDetailResponse result = bookService.registerBookByApi(apiRequest);

        // Then
        assertThat(result).isEqualTo(mockDetailResponse);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("API를 통한 도서 등록 - 실패 (이미 존재하는 ISBN)")
    void registerBookByApi_AlreadyExists_Failure() {
        // Given
        BookApiRegisterRequest apiRequest = new BookApiRegisterRequest(
                "API 테스트 책", "API 저자", "API 출판사", "API 내용", "2024-07-24", "9781234567890",
                25000, 20000, "API 설명", "http://api.cover.url", 50, 2L
        );
        Book book = Book.builder().isbn(apiRequest.getIsbn()).build();
        when(bookMapper.fromApiRequest(any(BookApiRegisterRequest.class))).thenReturn(book);
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> bookService.registerBookByApi(apiRequest))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessageContaining("이미 등록된 도서입니다.");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("API를 통한 도서 등록 - 실패 (카테고리 없음)")
    void registerBookByApi_CategoryNotFound_Failure() {
        // Given
        BookApiRegisterRequest apiRequest = new BookApiRegisterRequest(
                "API 테스트 책", "API 저자", "API 출판사", "API 내용", "2024-07-24", "9781234567890",
                25000, 20000, "API 설명", "http://api.cover.url", 50, 2L
        );
        Book book = Book.builder().isbn(apiRequest.getIsbn()).build();
        when(bookMapper.fromApiRequest(any(BookApiRegisterRequest.class))).thenReturn(book);
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.registerBookByApi(apiRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("카테고리가 존재하지 않습니다.");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("알라딘 api를 통한 도서 검색 성공")
    void searchBookFromExternalApiTest_Success(){
        //givnen
        String title = "어린 왕자";
        BookExternalResponse mockResponse = new BookExternalResponse(
                "어린 왕자",
                "description",
                "author",
                "출판사C",
                LocalDate.of(2024, 6, 13),
                "isbn",
                10000,
                9000,
                "img/path.jpg",
                "category1"
        );
        when(aladinBookApiService.searchBooksByTitle(title)).thenReturn(List.of(mockResponse));


        //when
        List<BookExternalResponse> result = bookService.searchBookFromExternalApi(title);

        //then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("어린 왕자");

        verify(aladinBookApiService, times(1)).searchBooksByTitle(title);
    }

    @Test
    @DisplayName("알라딘 api를 통한 도서 검색 실패")
    void searchBookFromExternalApiTest_Fail(){
        //given
        String title = "어린 왕자";
        when(aladinBookApiService.searchBooksByTitle(title)).thenReturn(Collections.emptyList());



        //when & then
        assertThatThrownBy(() -> bookService.searchBookFromExternalApi(title))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("제목과 일치하는 도서가 존재하지 않습니다.");

        verify(aladinBookApiService, times(1)).searchBooksByTitle(title);

    }


    @Test
    @DisplayName("등록된 도서 삭제 - 성공")
    void deleteBook_Success() {
        // given
        Long id = 10L;
        Book book = Book.builder()
                .id(id)
                .title("인어 공주")
                .description("인어 공주는...")
                .author("안데르센")
                .publisher("스웨덴출판사")
                .publishedDate(LocalDate.of(2016, 6, 16))
                .isbn("123456789EE")
                .regularPrice(new BigDecimal(15000))
                .salePrice(new BigDecimal(13000))
                .bookExtraInfo(new BookExtraInfo(Status.DELETED, true, 1))
                .build();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // when
        bookService.deleteBook(book.getId());

        // then
        verify(bookRepository, times(1)).findById(id);
        verify(bookSearchService, times(1)).deleteById(id);
        verify(bookRepository, times(1)).delete(book);
    }



    @Test
    @DisplayName("등록된 도서 삭제 : 실패 (등록된 도서 중 id에 해당하는 도서 존재 X)")
    void deleteBook_Failure() {
        //given
        Long id = 0L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> bookService.deleteBook(id))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("id : "+id+"에 해당하는 도서를 찾을 수 없습니다.");

        //then
        verify(bookRepository, never()).delete(any(Book.class));
        verify(bookRepository,times(1)).findById(id);
    }

    @Test
    @DisplayName("제목으로 도서 검색(성공)")
    void searchBookByTitle_Success() {
        //given
        String title = "헨젤과 그레텔";
        Book book = Book.builder()
                .id(1L)
                .title("인어 공주")
                .description("인어 공주는...")
                .author("안데르센")
                .publisher("스웨덴출판사")
                .publishedDate(LocalDate.of(2016, 6, 16))
                .isbn("123456789EE")
                .regularPrice(new BigDecimal(15000))
                .salePrice(new BigDecimal(13000))
                .bookExtraInfo(new BookExtraInfo(Status.DELETED, true, 1))
                .build();

        BookDetailResponse bookDetailResponse = new BookDetailResponse(
                1L,
                "헨젤과 그레텔",
                "목차1, 목차2, 목차3...",
                "설명",
                "그림형제",
                "한국출판사",
                LocalDate.of(1999, 9, 19),
                "0070ABC",
                new BigDecimal("10000"),
                new BigDecimal("6000"),
                false,
                new ArrayList<>(),
                10,
                "NORMAL"
        );

        //when
        when(bookRepository.findByTitleContaining(title)).thenReturn(List.of(book));
        when(bookResponseMapper.toBookDetailResponse(any())).thenReturn(bookDetailResponse);


        List<BookDetailResponse> bookDetailResponses = bookService.searchBookByTitle(title);

        assertThat(bookDetailResponses).hasSize(1);
        assertThat(bookDetailResponses.getFirst().getTitle()).isEqualTo("헨젤과 그레텔");

        verify(bookRepository,times(1)).findByTitleContaining(title);
    }

    @Test
    @DisplayName("제목으로 도서 검색(실패)")
    void searchBookByTitle_Failure(){

        String title = "존재하지 않는 제목";

        when(bookRepository.findByTitleContaining(any())).thenReturn(Collections.emptyList());


        //when & then
        assertThatThrownBy(() -> bookService.searchBookByTitle(title))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("제목과 일치하는 도서가 존재하지 않습니다.");

        //then
        verify(bookRepository, times(1)).findByTitleContaining(title);
        verify(bookResponseMapper, times(0)).toBookDetailResponse(any(Book.class));


    }

    @Test
    @DisplayName("도서 수정 - 성공")
    void updateBook_Success() {
        // given
        Long id = 9L;

        // 기존 Book
        Book updatedBook = Book.builder()
                .id(id)
                .title("인어 공주")
                .description("인어 공주는...")
                .author("안데르센")
                .publisher("스웨덴출판사")
                .publishedDate(LocalDate.of(2016, 6, 16))
                .isbn("123456789EE")
                .regularPrice(new BigDecimal(15000))
                .salePrice(new BigDecimal(13000))
                .bookExtraInfo(new BookExtraInfo(Status.NORMAL, true, 1))
                .build();

        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest(
                1L,
                "테스트 도서 제목",
                "테스트 저자",
                "테스트 출판사",
                "2024-01-01",
                "1234567890123",
                new BigDecimal("15000"),
                new BigDecimal("12000"),
                "이것은 설명입니다.",
                "이것은 목차입니다.",
                "http://example.com/image.jpg",
                10,
                "NORMAL",
                true,
                1L,
                2L,
                3L
        );

        Category mockCategory = mock(Category.class);
        BookCategory mockBookCategory = mock(BookCategory.class);
        BookImage mockBookImage = mock(BookImage.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(updatedBook));
        when(categoryRepository.findById(bookUpdateRequest.getCategoryId())).thenReturn(Optional.of(mockCategory));
        when(bookCategoryRepository.findByBookId(id)).thenReturn(Optional.of(mockBookCategory));
        when(bookImageRepository.findByBookIdAndImageType(id, ImageType.THUMBNAIL)).thenReturn(Optional.of(mockBookImage));

        bookService.updateBook(id, bookUpdateRequest);

        verify(bookRepository).findById(id);
        verify(categoryRepository).findById(bookUpdateRequest.getCategoryId());
        verify(bookCategoryRepository).findByBookId(id);
        verify(bookImageRepository).findByBookIdAndImageType(id, ImageType.THUMBNAIL);
        verify(mockBookCategory).setCategory(mockCategory);
        verify(mockBookImage).setImageUrl(bookUpdateRequest.getCover());
        verify(bookSearchService).syncBookToElasticsearch(updatedBook);
    }

    @Test
    @DisplayName("도서 수정(존재하지 않는 도서) - 실패")
    void updateBook_Failure() {
        Long id = 0L;
        BookUpdateRequest mockBookUpdateRequest = mock(BookUpdateRequest.class);

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(id, mockBookUpdateRequest))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("해당 ID(" + id + ")의 도서가 존재하지 않습니다.");

        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("도서 수정 - 실패 (카테고리 없음)")
    void updateBook_CategoryNotFound_Failure() {
        // Given
        Long id = 9L;
        Book updatedBook = Book.builder().id(id).bookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 10)).build();
        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest(
                1L, "테스트 도서 제목", "테스트 저자", "테스트 출판사", "2024-01-01", "1234567890123",
                new BigDecimal("15000"), new BigDecimal("12000"), "설명", "목차",
                "http://example.com/image.jpg", 10, "NORMAL", true, 1L, 2L, 3L
        );

        when(bookRepository.findById(id)).thenReturn(Optional.of(updatedBook));
        when(categoryRepository.findById(bookUpdateRequest.getCategoryId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.updateBook(id, bookUpdateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("카테고리가 존재하지 않습니다.");

        verify(bookRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).findById(bookUpdateRequest.getCategoryId());
    }

    @Test
    @DisplayName("도서 수정 - 실패 (BookCategory 없음)")
    void updateBook_BookCategoryNotFound_Failure() {
        // Given
        Long id = 9L;
        Book updatedBook = Book.builder().id(id).bookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 10)).build();
        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest(
                1L, "테스트 도서 제목", "테스트 저자", "테스트 출판사", "2024-01-01", "1234567890123",
                new BigDecimal("15000"), new BigDecimal("12000"), "설명", "목차",
                "http://example.com/image.jpg", 10, "NORMAL", true, 1L, 2L, 3L
        );

        Category mockCategory = mock(Category.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(updatedBook));
        when(categoryRepository.findById(bookUpdateRequest.getCategoryId())).thenReturn(Optional.of(mockCategory));
        when(bookCategoryRepository.findByBookId(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.updateBook(id, bookUpdateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("카테고리 정보가 없습니다.");

        verify(bookRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).findById(bookUpdateRequest.getCategoryId());
        verify(bookCategoryRepository, times(1)).findByBookId(id);
    }

    @Test
    @DisplayName("도서 수정 - 실패 (BookImage 없음)")
    void updateBook_BookImageNotFound_Failure() {
        // Given
        Long id = 9L;
        Book updatedBook = Book.builder().id(id).bookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 10)).build();
        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest(
                1L, "테스트 도서 제목", "테스트 저자", "테스트 출판사", "2024-01-01", "1234567890123",
                new BigDecimal("15000"), new BigDecimal("12000"), "설명", "목차",
                "http://example.com/image.jpg", 10, "NORMAL", true, 1L, 2L, 3L
        );

        Category mockCategory = mock(Category.class);
        BookCategory mockBookCategory = mock(BookCategory.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(updatedBook));
        when(categoryRepository.findById(bookUpdateRequest.getCategoryId())).thenReturn(Optional.of(mockCategory));
        when(bookCategoryRepository.findByBookId(id)).thenReturn(Optional.of(mockBookCategory));
        when(bookImageRepository.findByBookIdAndImageType(id, ImageType.THUMBNAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.updateBook(id, bookUpdateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("대표 이미지 정보가 없습니다.");

        verify(bookRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).findById(bookUpdateRequest.getCategoryId());
        verify(bookCategoryRepository, times(1)).findByBookId(id);
        verify(bookImageRepository, times(1)).findByBookIdAndImageType(id, ImageType.THUMBNAIL);
    }

    @Test
    @DisplayName("도서 수량 감소 - 성공")
    void deductBooksCount_Success() {
        // Given
        List<BookCountUpdateRequest> requests = new ArrayList<>();
        BookCountUpdateRequest req1 = new BookCountUpdateRequest();
        req1.setBookId(1L);
        req1.setBookCount(5);
        requests.add(req1);

        BookCountUpdateRequest req2 = new BookCountUpdateRequest();
        req2.setBookId(2L);
        req2.setBookCount(3);
        requests.add(req2);

        Book book1 = Book.builder().id(1L).bookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 10)).build();
        Book book2 = Book.builder().id(2L).bookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 5)).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(bookRepository.save(any(Book.class))).thenReturn(mock(Book.class));

        // When
        bookService.updateBooksCount(requests,"postive");

        // Then
        assertThat(book1.getBookExtraInfo().getCount()).isEqualTo(5);
        assertThat(book2.getBookExtraInfo().getCount()).isEqualTo(2);
        verify(bookRepository, times(1)).save(book1);
        verify(bookRepository, times(1)).save(book2);
    }

    @Test
    @DisplayName("도서 수량 감소 - 재고 부족 실패")
    void deductBooksCount_InsufficientStock_Failure() {
        // Given
        List<BookCountUpdateRequest> requests = new ArrayList<>();
        BookCountUpdateRequest req1 = new BookCountUpdateRequest();
        req1.setBookId(1L);
        req1.setBookCount(15);
        requests.add(req1);

        Book book1 = Book.builder().id(1L).bookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 10)).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        // When & Then
        assertThatThrownBy(() -> bookService.updateBooksCount(requests,"postive"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고 부족: 현재 재고=10, 요청 차감=15");
    }



    @Test
    @DisplayName("모든 도서 페이징 조회 - 성공")
    void getAllBooksByPaging_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = Collections.singletonList(Book.builder().id(1L).title("Test Book").build());
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        BookDetailResponse mockResponse = new BookDetailResponse();

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookResponseMapper.toBookDetailResponse(any(Book.class))).thenReturn(mockResponse);

        // When
        Page<BookDetailResponse> result = bookService.getAllBooksByPaging(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookResponseMapper, times(1)).toBookDetailResponse(any(Book.class));
    }

    @Test
    @DisplayName("모든 도서와 추가 정보 페이징 조회 - 성공")
    void getAllBooksWithExtraInfo_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .publishedDate(LocalDate.now())
                .bookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 10))
                .build();
        book.setBookImages(Collections.singletonList(new BookImage(book, "test.jpg", ImageType.THUMBNAIL)));
        book.setBookCategories(Collections.singleton(new BookCategory(book, new Category("Test Category"))));
        List<Book> books = Collections.singletonList(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        // Removed: BookDetailWithExtraInfoResponse mockResponse = BookDetailWithExtraInfoResponse.builder().id(1L).title("Test Book").build();

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        // When
        Page<BookDetailWithExtraInfoResponse> result = bookService.getAllBooksWithExtraInfo(pageable); // Directly call the service method

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L); // Verify properties directly
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Book"); // Verify properties directly
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("ID로 도서 검색 - 성공")
    void searchBookById_Success() {
        // Given
        Long bookId = 1L;
        Book book = Book.builder().id(bookId).title("Test Book").build();
        BookDetailResponse mockResponse = new BookDetailResponse();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookResponseMapper.toBookDetailResponse(any(Book.class))).thenReturn(mockResponse);

        // When
        BookDetailResponse result = bookService.searchBookById(bookId);

        // Then
        assertThat(result).isEqualTo(mockResponse);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookResponseMapper, times(1)).toBookDetailResponse(any(Book.class));
    }

    @Test
    @DisplayName("ID로 도서 검색 - 실패 (도서 없음)")
    void searchBookById_NotFound_Failure() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.searchBookById(bookId))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("해당 ID(" + bookId + ")의 도서가 존재하지 않습니다.");

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookResponseMapper, never()).toBookDetailResponse(any(Book.class));
    }

    @Test
    @DisplayName("ISBN으로 도서 검색 - 성공")
    void searchBookByIsbn_Success() {
        // Given
        String isbn = "1234567890123";
        Book book = Book.builder().isbn(isbn).title("Test Book").build();
        BookDetailResponse mockResponse = new BookDetailResponse();

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(bookResponseMapper.toBookDetailResponse(any(Book.class))).thenReturn(mockResponse);

        // When
        BookDetailResponse result = bookService.searchBookByIsbn(isbn);

        // Then
        assertThat(result).isEqualTo(mockResponse);
        verify(bookRepository, times(1)).findByIsbn(isbn);
        verify(bookResponseMapper, times(1)).toBookDetailResponse(any(Book.class));
    }

    @Test
    @DisplayName("ISBN으로 도서 검색 - 실패 (도서 없음)")
    void searchBookByIsbn_NotFound_Failure() {
        // Given
        String isbn = "1234567890123";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.searchBookByIsbn(isbn))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining("해당 isbn(" + isbn + ")의 도서가 존재하지 않습니다.");
    }
}