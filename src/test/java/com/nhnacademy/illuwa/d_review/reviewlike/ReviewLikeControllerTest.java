package com.nhnacademy.illuwa.d_review.reviewlike;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.reviewlike.controller.ReviewLikeController;
import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.service.Impl.ReviewLikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewLikeController.class)
public class ReviewLikeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewLikeServiceImpl reviewLikeService;

    private ReviewLikeResponse response;

    private Long bookId;
    private Long reviewId;
    private Long memberId;

    @BeforeEach
    void setUp() {
        Book book = new Book(
                10L,
                "이상한 책",
                "목차",
                "아무 설명",
                "이상한 사람",
                "무명출판",
                LocalDate.of(1970, 1, 1),
                "11111111111111111",
                100,
                100,
                false,
                "book.jpg",
                "카테고리없음"
        );

        Review review = new Review(
                1L,
                "리뷰 제목",
                "리뷰 내용",
                "/img/review.jpg",
                5,
                LocalDateTime.of(2099, 8, 22, 4, 17, 32),
                book,
                9999L
        );

        bookId = book.getId();
        reviewId = review.getReviewId();
        memberId = 7777L;
    }

    @Test
    @DisplayName("좋아요 설정")
    public void reviewLikeAddTest() throws Exception {
        // given
        response = new ReviewLikeResponse(true, 16L);
        given(reviewLikeService.addLike(eq(bookId), eq(reviewId), eq(memberId))).willReturn(response);

        // when & then
        mockMvc.perform(post("/books/{bookId}/reviews/{reviewId}/likes", bookId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likedByMe").value("true"))
                .andExpect(jsonPath("$.likeCount").value(16L));

        verify(reviewLikeService).addLike(eq(bookId), eq(reviewId), eq(memberId));
    }

    @Test
    @DisplayName("좋아요 취소")
    public void reviewLikeCancelTest() throws Exception {
        // given
        response = new ReviewLikeResponse(false, 3L);
        given(reviewLikeService.cancelLike(eq(reviewId), eq(memberId))).willReturn(response);

        // when & then
        mockMvc.perform(delete("/books/{bookId}/reviews/{reviewId}/likes", bookId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likedByMe").value("false"))
                .andExpect(jsonPath("$.likeCount").value(3L));

        verify(reviewLikeService).cancelLike(eq(reviewId), eq(memberId));
    }

    @Test
    @DisplayName("좋아요 정보 가져오기")
    public void getReviewLikeInfoTest() throws Exception {
        // given
        response = new ReviewLikeResponse(true, 302L);
        given(reviewLikeService.getLikeInfo(eq(reviewId), eq(memberId))).willReturn(response);

        // when & then
        mockMvc.perform(get("/books/{bookId}/reviews/{reviewId}/likes", bookId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likedByMe").value("true"))
                .andExpect(jsonPath("$.likeCount").value(302L));

        verify(reviewLikeService).getLikeInfo(eq(reviewId), eq(memberId));
    }
}
