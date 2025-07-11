package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookImageRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookSearchRepository;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import com.nhnacademy.illuwa.d_book.category.repository.category.CategoryRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
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
    BookSearchRepository bookSearchRepository;


    @InjectMocks
    BookService bookService;


    static BookRegisterRequest bookRegisterRequest;

    @BeforeAll
    static void setUp() {
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
    @DisplayName("도서 등록 성공")
    void registerBookTest_Success(){
        Book mockBook = Book.builder()
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
                "어린 왕자",
                "contents",
                "description",
                "author",
                "출판사A",
                LocalDate.of(2012,12,21),
                "012345",
                new BigDecimal(10000),
                new BigDecimal(9000),
                true,
                "imgUrl"
        );

        Long categoryId = 1L;



        Category mockCategory = new Category("테스트 카테고리");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(bookMapper.toBookEntity(bookRegisterRequest)).thenReturn(mockBook);
        when(bookRepository.existsByIsbn("123456789EE")).thenReturn(false);
        when(bookResponseMapper.toBookDetailResponse(mockBook)).thenReturn(bookDetailResponse);
        when(bookRepository.save(any())).thenReturn(mockBook);


        BookDetailResponse result = bookService.registerBook(bookRegisterRequest);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("어린 왕자");
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));

    }


    @Test
    @DisplayName("도서 등록 실패 - 이미 등록된 도서")
    void registerBookTest_Fail_AlreadyExists(){

        Book book = Book.builder()
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

        Category category = new Category("name");


        given(bookMapper.toBookEntity(any(BookRegisterRequest.class))).willReturn(book);
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));


        //when & then
        assertThatThrownBy(() -> bookService.registerBook(bookRegisterRequest))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessage("이미 등록된 도서입니다.");


        verify(bookRepository, times(0)).save(any(Book.class));

    }


    @Test
    @DisplayName("등록된 도서 삭제 - 성공")
    void deleteBook_Success() {
        //given
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

        //when
        bookService.deleteBook(book.getId());

        //then
        verify(bookRepository,times(1)).findById(id);

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
                0L,
                "헨젤과 그레텔",
                "목차1, 목차2, 목차3...",
                "설명",
                "그림형제",
                "한국출판사",
                LocalDate.of(1999, 9, 19),
                "0070ABC",
                new BigDecimal(10000),
                new BigDecimal(6000),
                false,
                "imgUrl"
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
        Long id = 9L;

        Book updatedBook = Book.builder()
                .id(9L)
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

        when(bookRepository.findById(id)).thenReturn(Optional.of(updatedBook));

        when(categoryRepository.findById(bookUpdateRequest.getCategoryId()))
                .thenReturn(Optional.of(mock(Category.class)));

        when(bookCategoryRepository.findByBookId(id))
                .thenReturn(Optional.of(mock(BookCategory.class)));

        // 실행
        bookService.updateBook(id, bookUpdateRequest);

        // 검증
        verify(bookRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).findById(bookUpdateRequest.getCategoryId());
        verify(bookCategoryRepository, times(1)).findByBookId(id);
    }

    @Test
    @DisplayName("도서 수정(존재하지 않는 도서) - 실패")
    void updateBook_Failure() {
        Long id = 0L;
        BookUpdateRequest mockBookUpdateRequest = mock(BookUpdateRequest.class);

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(id,mockBookUpdateRequest))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("해당 도서를 찾을 수 없습니다. id: "+id);

        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, never()).save(any(Book.class));


    }
}
