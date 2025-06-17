package com.nhnacademy.illuwa.d_book.book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.mapper.BookExternalMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.book.service.AladinBookApiService;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {
    @Mock
    AladinBookApiService aladinBookApiService;
    @Mock
    BookRepository bookRepository;

    @Mock
    BookExternalMapper bookExternalMapper;

    @Mock
    BookResponseMapper bookResponseMapper;



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
        assertThat(result.get(0).getTitle()).isEqualTo("어린 왕자");

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

        //givnen
        String isbn = "012345";
        BookExternalResponse mockResponse = new BookExternalResponse(
                "어린 왕자",
                "description",
                "author",
                "B출판사",
                LocalDate.of(2024, 6, 13),
                "012345",
                20000,
                10000,
                "imgUrl",
                "category"
        );

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
                true,
                "imgUrl",
                "category"
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
                "imgUrl",
                "categoryName"
        );

        when(aladinBookApiService.findBookByIsbn("012345")).thenReturn(mockResponse);
        when(bookRepository.existsByIsbn("012345")).thenReturn(false);
        when(bookExternalMapper.toBookEntity(mockResponse)).thenReturn(mockBook);
        when(bookResponseMapper.toBookDetailResponse(mockBook)).thenReturn(bookDetailResponse);

        //when
        BookDetailResponse result = bookService.registerBook(isbn);

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

        when(bookRepository.existsByIsbn("012345")).thenReturn(true);

        //when & then
        assertThatThrownBy(() -> bookService.registerBook(isbn))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessage("이미 등록된 도서입니다.");

        verify(bookRepository, times(1)).existsByIsbn(isbn);
        verify(aladinBookApiService, times(0)).findBookByIsbn(isbn);
        verify(bookRepository, times(0)).save(any(Book.class));

    }

    @Test
    @DisplayName("도서 등록 실패 - ISBN과 일치하는 도서 없음")
    void registerBookTest_Fail_NotFoundIsbn(){

        //givnen
        String isbn = "012345";
        when(aladinBookApiService.findBookByIsbn("012345")).thenReturn(null);


        //when & then
        assertThatThrownBy(() -> bookService.registerBook(isbn))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessage("ISBN과 일치하는 도서가 없습니다.");

        //then
        verify(bookRepository, times(1)).existsByIsbn(isbn);
        verify(aladinBookApiService, times(1)).findBookByIsbn(isbn);
        verify(bookRepository, times(0)).save(any(Book.class));

    }

}
