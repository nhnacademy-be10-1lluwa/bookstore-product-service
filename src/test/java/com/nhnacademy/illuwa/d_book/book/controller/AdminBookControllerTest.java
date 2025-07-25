package com.nhnacademy.illuwa.d_book.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookApiRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookCountUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
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
    BookService bookService;
    @MockBean
    TagService tagService;


    @Autowired
    ObjectMapper objectMapper;


    static BookRegisterRequest bookRegisterRequest;

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
                null,
                10,              // count
                1L               // categoryId
        );
    }

    @Test
    @DisplayName("도서 직접 등록 - 성공")
    void registerBookDirectly_Success() throws Exception {
        // Given
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
                objectMapper.writeValueAsBytes(bookRegisterRequest)
        );

        // When & Then
        mockMvc.perform(multipart("/api/admin/books")
                        .file(requestPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated());

        verify(bookService, times(1)).registgerBookDirectly(any(BookRegisterRequest.class), any(MultipartFile.class));
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
                objectMapper.writeValueAsBytes(bookRegisterRequest)
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
    @DisplayName("API를 통한 도서 등록 - 성공")
    void registerBookByApi_Success() throws Exception {
        // Given
        BookApiRegisterRequest apiRequest = new BookApiRegisterRequest(
                "API 테스트 책", "API 저자", "API 출판사", "API 내용", "2024-07-24", "9781234567890",
                25000, 20000, "API 설명", "http://api.cover.url", 50, 2L
        );
        BookDetailResponse mockResponse = new BookDetailResponse();
        given(bookService.registerBookByApi(any(BookApiRegisterRequest.class))).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/admin/books/external")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiRequest)))
                .andExpect(status().isCreated());

        verify(bookService, times(1)).registerBookByApi(any(BookApiRegisterRequest.class));
    }

    @Test
    @DisplayName("API를 통한 도서 등록 - 실패 (이미 존재하는 ISBN)")
    void registerBookByApi_AlreadyExists_Failure() throws Exception {
        // Given
        BookApiRegisterRequest apiRequest = new BookApiRegisterRequest(
                "API 테스트 책", "API 저자", "API 출판사", "API 내용", "2024-07-24", "9781234567890",
                25000, 20000, "API 설명", "http://api.cover.url", 50, 2L
        );
        willThrow(new BookAlreadyExistsException("이미 등록된 도서입니다."))
                .given(bookService)
                .registerBookByApi(any(BookApiRegisterRequest.class));

        // When & Then
        mockMvc.perform(post("/api/admin/books/external")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiRequest)))
                .andExpect(status().isConflict())
                .andDo(print());

        verify(bookService, times(1)).registerBookByApi(any(BookApiRegisterRequest.class));
    }

    @Test
    @DisplayName("도서 삭제 - 성공")
    void deleteBook() throws Exception {
        // given
        Long id = 11L;

        // when & then
        mockMvc.perform(delete("/api/admin/books/{id}",id))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(id);
    }

    @Test
    @DisplayName("도서 정보 업데이트 - 성공")
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
                        .content(objectMapper.writeValueAsString(bookUpdateRequest)))
                .andExpect(status().isNoContent());

        verify(bookService).updateBook(any(), any());
    }

    @Test
    @DisplayName("도서 및 추가 정보 목록 조회 - 성공")
    void getBooksWithExtraInfo_Success() throws Exception {
        // Given
        Page<BookDetailResponse> mockPage = new PageImpl<>(Collections.singletonList(new BookDetailResponse()));
        given(bookService.getAllBooksWithExtraInfo(any(Pageable.class))).willReturn(new PageImpl<>(Collections.emptyList()));

        // When & Then
        mockMvc.perform(get("/api/admin/books/details")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getAllBooksWithExtraInfo(any(Pageable.class));
    }

    @Test
    @DisplayName("도서 수량 감소 - 성공")
    void deductBooksCount_Success() throws Exception {
        // Given
        List<BookCountUpdateRequest> requests = Collections.singletonList(new BookCountUpdateRequest(1L, 5));

        // When & Then
        mockMvc.perform(put("/api/admin/books/update/bookCount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).updateBooksCount(anyList());
    }

    @Test
    @DisplayName("도서 수량 복원 - 성공")
    void restoreBooksCount_Success() throws Exception {
        // Given
        List<BookCountUpdateRequest> requests = Collections.singletonList(new BookCountUpdateRequest(1L, 5));

        // When & Then
        mockMvc.perform(put("/api/admin/books/restore/bookCount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).restoreBooksCount(anyList());
    }

    @Test
    @DisplayName("도서에 태그 추가 - 성공")
    void addTagToBook_Success() throws Exception {
        // Given
        Long bookId = 1L;
        Long tagId = 1L;

        // When & Then
        mockMvc.perform(post("/api/admin/books/{bookId}/tags/{tagId}", bookId, tagId))
                .andExpect(status().isOk());

        verify(tagService, times(1)).addTagToBook(bookId, tagId);
    }

    @Test
    @DisplayName("도서에서 태그 제거 - 성공")
    void removeTagFromBook_Success() throws Exception {
        // Given
        Long bookId = 1L;
        Long tagId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/admin/books/{bookId}/tags/{tagId}", bookId, tagId))
                .andExpect(status().isNoContent());

        verify(tagService, times(1)).removeTagFromBook(bookId, tagId);
    }

    @Test
    @DisplayName("도서에 연결된 태그 조회 - 성공")
    void getTagsByBookId_Success() throws Exception {
        // Given
        Long bookId = 1L;
        List<TagResponse> mockTags = Collections.singletonList(TagResponse.builder().id(1L).name("Test Tag").build());
        given(tagService.getTagsByBookId(bookId)).willReturn(mockTags);

        // When & Then
        mockMvc.perform(get("/api/admin/books/{bookId}/tags", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Tag"));

        verify(tagService, times(1)).getTagsByBookId(bookId);
    }
}
