package com.nhnacademy.illuwa.d_book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.response.BestSellerResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import io.swagger.v3.oas.annotations.Operation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private AladinBookApiService aladinBookApiService;

    @MockBean
    private BookMapper bookMapper;

    static BookRegisterRequest bookRegisterRequest;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookResponseMapper bookResponseMapper;

    @MockBean
    private BookSearchService bookSearchService;

    @Test
    @DisplayName("ISBN으로 DB 도서 검색 - 성공")
    void getBookByIsbn_Success() throws Exception {
        // Given
        String isbn = "1234567890123";
        BookDetailResponse mockResponse = new BookDetailResponse();
        given(bookService.searchBookByIsbn(isbn)).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/books/isbn/{isbn}", isbn))
                .andExpect(status().isOk());

        verify(bookService, times(1)).searchBookByIsbn(isbn);
    }

    @Test
    @DisplayName("ISBN으로 DB 도서 검색 - 실패 (도서 없음)")
    void getBookByIsbn_NotFound() throws Exception {
        // Given
        String isbn = "1234567890123";
        given(bookService.searchBookByIsbn(isbn)).willThrow(new NotFoundBookException("도서를 찾을 수 없습니다."));

        // When & Then
        mockMvc.perform(get("/api/books/isbn/{isbn}", isbn))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).searchBookByIsbn(isbn);
    }

    @Test
    void searchBookById() throws Exception {
        // given
        Long id = 11L;

        // when & then
        mockMvc.perform(get("/api/books/{id}",id))
                .andExpect(status().isOk());

        verify(bookService).searchBookById(id);
    }

    @Test
    @DisplayName("도서 목록 조회 - 일반 도서")
    void getBooks_Normal() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookDetailResponse> mockPage = new PageImpl<>(List.of(new BookDetailResponse()));
        given(bookService.getAllBooksByPaging(any(Pageable.class))).willReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getAllBooksByPaging(any(Pageable.class));
        verify(aladinBookApiService, never()).getBestSeller();
    }

    @Test
    @DisplayName("도서 목록 조회 - 베스트셀러")
    void getBooks_BestSeller() throws Exception {
        // Given
        List<BestSellerResponse> mockBestSellers = List.of(new BestSellerResponse());
        given(aladinBookApiService.getBestSeller()).willReturn(mockBestSellers);

        // When & Then
        mockMvc.perform(get("/api/books")
                        .param("type", "bestseller"))
                .andExpect(status().isOk());

        verify(aladinBookApiService, times(1)).getBestSeller();
        verify(bookService, never()).getAllBooksByPaging(any(Pageable.class));
    }
}
