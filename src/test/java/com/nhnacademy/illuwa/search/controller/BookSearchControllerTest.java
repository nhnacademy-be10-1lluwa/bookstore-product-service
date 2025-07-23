package com.nhnacademy.illuwa.search.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.search.document.BookDocument;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookSearchController.class)
class BookSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookSearchService bookSearchService;

    @Test
    @DisplayName("키워드로 책 검색")
    void searchBooksByKeyword() throws Exception {
        Page<BookDocument> page = new PageImpl<>(Collections.singletonList(new BookDocument()));
        when(bookSearchService.searchByKeyword(anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/search").param("keyword", "test"))
                .andExpect(status().isOk());

        verify(bookSearchService).searchByKeyword(anyString(), any(PageRequest.class));
    }

    @Test
    @DisplayName("책 인덱싱")
    void indexBook() throws Exception {
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookDocument())))
                .andExpect(status().isOk());

        verify(bookSearchService).save(any(BookDocument.class));
    }

    @Test
    @DisplayName("인덱스 삭제")
    void deleteIndex() throws Exception {
        mockMvc.perform(delete("/api/search/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookSearchService).deleteById(1L);
    }

    @Test
    @DisplayName("전체 책 동기화")
    void syncAllBooks() throws Exception {
        mockMvc.perform(post("/api/search/_sync"))
                .andExpect(status().isOk());

        verify(bookSearchService).syncAllBooksToElasticsearch();
    }

    @Test
    @DisplayName("카테고리로 책 검색")
    void searchBooksByCategory() throws Exception {
        Page<BookDocument> page = new PageImpl<>(Collections.singletonList(new BookDocument()));
        when(bookSearchService.searchByCategory(anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/search/category").param("category", "test"))
                .andExpect(status().isOk());

        verify(bookSearchService).searchByCategory(anyString(), any(PageRequest.class));
    }
}
