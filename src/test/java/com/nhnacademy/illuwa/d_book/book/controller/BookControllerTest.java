package com.nhnacademy.illuwa.d_book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    @DisplayName("책 검색 성공 - Title")
    void searchBook_Success() throws Exception {
        //given
        BookDetailResponse bookDetailResponse = new BookDetailResponse(
                0L,
                "어린 왕자",
                "contents",
                "description",
                "author",
                "출판사",
                LocalDate.of(2024, 6, 13),
                "010000",
                new BigDecimal(10000),
                new BigDecimal(9000),
                true,
                "img/path.jpg"
        );

        when(bookService.searchBookByTitle("어린 왕자")).thenReturn(List.of(bookDetailResponse));


        //when & then
        mockMvc.perform(get("/api/books/search")
                        .param("title","어린 왕자")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$[0].isbn").value("010000"))
                .andExpect(status().isOk());

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
    void getRegisteredBooks() throws Exception {
        // given
        List<BookDetailResponse> mockBooks = List.of(
                new BookDetailResponse()
        );
        given(bookService.getAllBooks()).willReturn(mockBooks);

        // when & then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockBooks.size())); // JSON 배열 길이 검증

        verify(bookService).getAllBooks(); // 여기 맞게 verify
    }



}
