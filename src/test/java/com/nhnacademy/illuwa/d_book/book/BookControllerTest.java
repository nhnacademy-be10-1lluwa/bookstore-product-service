package com.nhnacademy.illuwa.d_book.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.controller.AdminBookController;
import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.BookSearchRequest;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.service.AladinBookApiService;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
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


    @Test
    @DisplayName("책 검색 성공")
    void searchBook_Success() throws Exception {
        //given
        BookSearchRequest searchRequestDto = new BookSearchRequest("어린 왕자");
        BookExternalResponse responseDto = new BookExternalResponse(
                "어린 왕자",
                "author",
                LocalDate.of(2024, 6, 13),
                "description",
                "010000",
                10000,
                90000,
                "img/path.jpg",
                "category1",
                "출판사"
        );

        String json = objectMapper.writeValueAsString(searchRequestDto);
        given(bookService.searchBookFromExternalApi(searchRequestDto.getTitle())).willReturn(List.of(responseDto));


        //when & then
        mockMvc.perform(post("/admin/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(jsonPath("$[0].isbn").value("010000"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("책 검색 실패")
    void searchBook_Fail() throws Exception {
        // given
        String searchTitle = "세상에 없는 책";
        BookSearchRequest requestDto = new BookSearchRequest(searchTitle);

        given(bookService.searchBookFromExternalApi(searchTitle))
                .willThrow(new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다."));

        // when
     mockMvc.perform(post("/admin/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andDo(print());


        verify(bookService).searchBookFromExternalApi(searchTitle);
    }

    @Test
    @DisplayName("책 등록 성공")
    void registgerBook_Success() throws Exception {
        //given
        BookRegisterRequest registerRequest = new BookRegisterRequest("0100AF");
        String testIsbn = "0100AF";

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
                "abc/def/g.jpg",
                "Category"
        );

        String json = objectMapper.writeValueAsString(registerRequest);
        given(bookService.registerBook(registerRequest.getISBN())).willReturn(responseDto);


        //when & then
        mockMvc.perform(post("/admin/books/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.isbn").value("0100AF"))
                .andExpect(status().isOk());

        verify(bookService).registerBook(testIsbn);

    }

    @Test
    @DisplayName("책 등록 실패 - 이미 존재하는 도서")
    void registgerBook_Failure() throws Exception {
        //given
        String alreadyExistsIsbn = "이미 등록된 도서의 ISBN";
        BookRegisterRequest registerRequest = new BookRegisterRequest(alreadyExistsIsbn);

        String json = objectMapper.writeValueAsString(registerRequest);

        given(bookService.registerBook(registerRequest.getISBN())).willThrow(new BookAlreadyExistsException("이미 등록된 도서"));


        //when & then
        mockMvc.perform(post("/admin/books/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andDo(print());

        verify(bookService).registerBook(alreadyExistsIsbn);

    }

}
