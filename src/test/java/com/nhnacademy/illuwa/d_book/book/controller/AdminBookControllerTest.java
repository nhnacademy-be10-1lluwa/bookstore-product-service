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
    @DisplayName("책 등록 성공")
    @Disabled
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
                new BigDecimal(10000),
                new BigDecimal(9000),
                true,
                "abc/def/g.jpg"
        );

        Book book =Book.builder()
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
                "목차",
                "description",
                "author",
                "출판사A",
                LocalDate.of(2024, 6, 13),
                "0100AF",
                new BigDecimal(10000),
        new BigDecimal(9000),
                true,
                "image/url"
        );


        String json = objectMapper.writeValueAsString(registerRequest);
        given(bookRepository.existsByIsbn(isbn)).willReturn(true);
        given(bookService.registerBook(any(BookRegisterRequest.class))).willReturn(responseDto);
        given(bookResponseMapper.toBookDetailResponse(book)).willReturn(bookDetailResponse);



        //when & then
        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.isbn").value("0100AF"))
                .andExpect(status().isCreated());

        verify(bookService).registerBook(any(BookRegisterRequest.class));
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
                .andExpect(status().isNotFound())
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

    @Test
    void updateBook() throws Exception {
        // given
        Long id = 11L;

        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest("contents","des",100,true);


        // when & then

        mockMvc.perform(patch("/api/admin/books/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookUpdateRequest)))
                .andExpect(status().isNoContent());


        verify(bookService).updateBook(any(),any());

    }
}