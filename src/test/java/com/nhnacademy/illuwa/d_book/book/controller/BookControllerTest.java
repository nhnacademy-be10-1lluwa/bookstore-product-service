package com.nhnacademy.illuwa.d_book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(AdminBookController.class)
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
                "http://image.com/prince.jpg",
                3,
                2L
        );
    }


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
                10000,
                9000,
                true,
                "img/path.jpg"
        );

        when(bookService.searchBookByTitle("어린 왕자")).thenReturn(List.of(bookDetailResponse));


        //when & then
        mockMvc.perform(get("/admin/books/search")
                        .param("title","어린 왕자")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$[0].isbn").value("010000"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("책 등록 성공")
    void registgerBook_Success() throws Exception {
        //given
        BookRegisterRequest registerRequest = bookRegisterRequest;
        String isbn = "mockIsbn";

        BookDetailResponse responseDto = new BookDetailResponse(
                1L,
                "어린 왕자",
                "목차",
                "description",
                "author",
                "출판사A",
                LocalDate.of(2024, 6, 13),
                "0100AF",
                10000,
                90000,
                true,
                "abc/def/g.jpg"
        );

        Book book = new Book(
                1L,
                "어린 왕자",
                "목차",
                "description",
                "author",
                "출판사A",
                LocalDate.of(2024, 6, 13),
                "0100AF",
                10000,
                90000,
                null,
                new BookExtraInfo(Status.DELETED,true,3)
        );

        BookDetailResponse bookDetailResponse = new BookDetailResponse(
                1L,
                "어린 왕자",
                "목차",
                "description",
                "author",
                "출판사A",
                LocalDate.of(2024, 6, 13),
                "0100AF",
                10000,
                90000,
                true,
                "image/url"
        );


        String json = objectMapper.writeValueAsString(registerRequest);
        given(bookRepository.existsByIsbn(isbn)).willReturn(true);
        given(bookService.registerBook(any(BookRegisterRequest.class))).willReturn(responseDto);
        given(bookResponseMapper.toBookDetailResponse(book)).willReturn(bookDetailResponse);



        //when & then
        mockMvc.perform(post("/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.isbn").value("0100AF"))
                .andExpect(status().isOk());

        verify(bookService).registerBook(any(BookRegisterRequest.class));
    }

    @Test
    @DisplayName("책 등록 실패 - 해당 도서가 이미 존재")
    void registgerBook_Failure() throws Exception {

        //given
        Book book = new Book();
        BookRegisterRequest request = new BookRegisterRequest(
                "제목",
                "목차",
                "설명",
                "저자",
                "출판사",
                LocalDate.of(2024, 6, 13),
                "내용",
                10000,
                8000,
                "http://cover.url",
                3,
                2L
        );


        String json = objectMapper.writeValueAsString(bookRegisterRequest);

        given(bookService.registerBook(any(BookRegisterRequest.class)))
                .willThrow(new BookAlreadyExistsException("이미 등록된 도서입니다."));

        //when & then
        mockMvc.perform(post("/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andDo(print());


    }
}
