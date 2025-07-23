package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.response.SimpleBookResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookLikeController.class)
class BookLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookLikeService bookLikeService;

    @Test
    @DisplayName("내가 좋아요 누른 책인지 확인")
    void isLikedByMe() throws Exception {
        when(bookLikeService.isLikedByMe(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/book-likes")
                        .param("book-id", "1")
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk());

        verify(bookLikeService).isLikedByMe(1L, 1L);
    }

    @Test
    @DisplayName("책 좋아요 토글")
    void toggleBookLikes() throws Exception {
        mockMvc.perform(post("/api/book-likes")
                        .param("book-id", "1")
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk());

        verify(bookLikeService).toggleBookLikes(1L, 1L);
    }

    @Test
    @DisplayName("내가 좋아요 누른 책 목록 조회")
    void getLikedBooksByMember() throws Exception {
        Page<SimpleBookResponse> page = new PageImpl<>(Collections.singletonList(new SimpleBookResponse()));
        when(bookLikeService.getLikedBooksByMember(anyLong(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/book-likes/list")
                        .param("page", "0")
                        .param("size", "10")
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk());

        verify(bookLikeService).getLikedBooksByMember(anyLong(), any(PageRequest.class));
    }
}
