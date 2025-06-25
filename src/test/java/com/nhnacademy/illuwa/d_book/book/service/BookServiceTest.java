package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.mapper.BookExternalMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookImageRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


    static BookRegisterRequest bookRegisterRequest;

    @BeforeAll
    static void setUp() {
        bookRegisterRequest = new BookRegisterRequest(
                "어린 왕자",
                "목차",
                "설명",
                "생텍쥐페리",
                "출판사A",
                LocalDate.of(2024, 6, 25),
                "9780123456789",
                15000,
                12000,
                "http://image.com/prince.jpg"
        );
    }


    @InjectMocks
    BookService bookService;

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


        Book mockBook = new Book(
                null,
                "어린 왕자",
                "목차",
                "description",
                "author",
                "publisher",
                LocalDate.of(2012,10,12),
                "012345",
                10000,
                9000,
                null,
                new BookExtraInfo(Status.DELETED,true,1)
        );

        BookDetailResponse bookDetailResponse = new BookDetailResponse(
                1L,
                "어린 왕자",
                "contents",
                "description",
                "author",
                "출판사A",
                LocalDate.of(2012,12,21),
                "012345",
                10000,
                9000,
                true,
                "imgUrl"
        );


        when(bookMapper.toBookEntity(bookRegisterRequest)).thenReturn(mockBook);
        when(bookRepository.existsByIsbn("012345")).thenReturn(false);
        when(bookResponseMapper.toBookDetailResponse(mockBook)).thenReturn(bookDetailResponse);

        BookDetailResponse result = bookService.registerBook(bookRegisterRequest);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("어린 왕자");

    }


    @Test
    @DisplayName("도서 등록 실패 - 이미 등록된 도서")
    void registerBookTest_Fail_AlreadyExists(){
        //givnen
        String isbn = "012345";

        BookExternalResponse mockResponse = new BookExternalResponse(
                "어린 왕자",
                "description",
                "author",
                "B출판사",
                LocalDate.of(2024, 6, 13),
                "isbn",
                20000,
                10000,
                "imgUrl",
                "category"
        );

        Book book = new Book(
                11L,
                "어린 왕자",
                "contents",
                "des",
                "작가1",
                "출판사",
                LocalDate.of(2024, 6, 13),
                "isbn",
                20000,
                10000,
                null,
                null
        );


        given(bookMapper.toBookEntity(any(BookRegisterRequest.class))).willReturn(book);
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);


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
        Book book = new Book(
                10L,
                "어린 왕자",
                "contents",
                "description",
                "author",
                "B출판사",
                LocalDate.of(2024, 6, 13),
                "00800ABZ",
                20000,
                10000,
                null,
                new BookExtraInfo(Status.DELETED,true,1)
            );

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
        Book book = new Book(
                0L,
                "헨젤과 그레텔",
                "목차1, 목차2, 목차3...",
                "설명",
                "그림형제",
                "한국출판사",
                LocalDate.of(1999, 9, 19),
                "0070ABC",
                10000,
                6000,
                null,
                new BookExtraInfo(Status.DELETED,true,1)
                );

        BookDetailResponse bookDetailResponse = new BookDetailResponse(
                0L,
                "헨젤과 그레텔",
                "목차1, 목차2, 목차3...",
                "설명",
                "그림형제",
                "한국출판사",
                LocalDate.of(1999, 9, 19),
                "0070ABC",
                10000,
                6000,
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

        Book updatedBook = new Book(
                9L,
                "어린 왕자",
                "contents",
                "description",
                "author",
                "B출판사",
                LocalDate.of(2024, 6, 13),
                "00800ABZ",
                20000,
                10000,
                null,
                new BookExtraInfo(Status.DELETED,true,1)
        );

        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest(
                "수정된 목차",
                "수정된 제목",
                10900,
                false
        );

        when(bookRepository.findById(id)).thenReturn(Optional.of(updatedBook));

        bookService.updateBook(id,bookUpdateRequest);

        verify(bookRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("도서 수정(존재하지 않는 도서) - 실패")
    void updateBook_Failure() {
        Long id = 0L;
        BookUpdateRequest mockBookUpdateRequest = mock(BookUpdateRequest.class);

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(id,mockBookUpdateRequest))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("해당 도서는 존재하지 않아서 수정이 불가능합니다.");

        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, never()).save(any(Book.class));


    }
}
