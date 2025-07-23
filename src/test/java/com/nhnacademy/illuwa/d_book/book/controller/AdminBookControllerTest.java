package com.nhnacademy.illuwa.d_book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.mapper.BookMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.book.service.BookImageService;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.d_book.category.service.BookCategoryService;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import com.nhnacademy.illuwa.infra.apiclient.AladinBookApiService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookController.class)
class AdminBookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookMapper bookMapper;

    @MockBean
    private AladinBookApiService aladinBookApiService;

    @MockBean
    BookService bookService;
    @MockBean
    BookCategoryService bookCategoryService;
    @MockBean
    BookImageService bookImageService;
    @MockBean
    TagService tagService;


    @Autowired
    ObjectMapper objectMapper;




    static BookRegisterRequest bookRegisterRequest;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookResponseMapper bookResponseMapper;


    @BeforeAll
    static void setUp() {
        bookRegisterRequest = new BookRegisterRequest(
                "테스트 책 제목",
                "홍길동",
                "테스트 출판사",
                "테스트 내용",
                "2025-07-10", // pubDate (String)
                "1234567890123", // isbn
                20000,           // regularPrice
                15000,           // salePrice
                "테스트 설명입니다.",
                new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "fake-image-content".getBytes()),
                10,              // count
                1L               // categoryId
        );
    }


    

    @Test
    @DisplayName("책 등록 실패 - 해당 도서가 이미 존재")
    void registerBook_Failure() throws Exception {
        // given
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                """
                {
                  "title": "테스트 책 제목",
                  "author": "홍길동",
                  "publisher": "테스트 출판사",
                  "contents": "테스트 내용",
                  "pubDate": "2025-07-10",
                  "isbn": "1234567890123",
                  "regularPrice": 20000,
                  "salePrice": 15000,
                  "description": "테스트 설명입니다.",
                  "count": 10,
                  "categoryId": 1
                }
                """.getBytes()
        );

        willThrow(new BookAlreadyExistsException("이미 등록된 도서입니다."))
                .given(bookService)
                .registgerBookDirectly(any(BookRegisterRequest.class), any(MultipartFile.class));

        mockMvc.perform(multipart("/api/admin/books")
                        .file(requestPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isConflict())
                .andDo(print());
    }





    @Test
    void deleteBook() throws Exception {
        // given
        Long id = 11L;

        // when & then
        mockMvc.perform(delete("/api/admin/books/{id}",id))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(id);
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<Void> updateBook(
//            @PathVariable Long id,
//            @RequestBody BookUpdateRequest requestDto
//    ) {
//        bookService.updateBook(id, requestDto);
//        return ResponseEntity.noContent().build();
//    }

    @Test
    void updateBook() throws Exception {
        // given
        Long id = 11L;

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

        // when & then
        mockMvc.perform(patch("/api/admin/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookUpdateRequest)))
                .andExpect(status().isNoContent());

        verify(bookService).updateBook(any(), any());
    }
}