package com.nhnacademy.illuwa.d_book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.response.BestSellerResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
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

//    @Test
//    @DisplayName("책 검색 성공 - Title")
//    void searchBook_Success() throws Exception {
//        //given
//        BookDetailResponse bookDetailResponse = new BookDetailResponse(
//                0L,
//                "어린 왕자",
//                "contents",
//                "description",
//                "author",
//                "출판사",
//                LocalDate.of(2024, 6, 13),
//                "010000",
//                new BigDecimal(10000),
//                new BigDecimal(9000),
//                true,
//                "img/path.jpg"
//        );
//
//        when(bookService.searchBookByTitle("어린 왕자")).thenReturn(List.of(bookDetailResponse));
//
//
//        //when & then
//        mockMvc.perform(get("/api/books/search")
//                        .param("title","어린 왕자")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//    }


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
        Pageable pageable = PageRequest.of(0, 10); // 테스트용 페이징
        Page<BookDetailResponse> mockPage = new PageImpl<>(List.of(new BookDetailResponse()));


        given(bookService.getAllBooksByPaging(any(Pageable.class))).willReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());

        verify(bookService).getAllBooksByPaging(any(Pageable.class)); // 여기 맞게 verify
    }




//    @Operation(summary = "도서 목록 조회 및 검색 (통합)")
//    @GetMapping
//    public ResponseEntity<?> getBooks(@RequestParam(required = false) String type,Pageable pageable) {
//
//        if ("bestseller".equalsIgnoreCase(type)) {
//            List<BestSellerResponse> bestSellerList = aladinBookApiService.getBestSeller();
//            return ResponseEntity.ok(bestSellerList);
//        }
//
//        Page<BookDetailResponse> allBooksPaged = bookService.getAllBooksByPaging(pageable);
//        return ResponseEntity.ok(allBooksPaged);

//    }



}
