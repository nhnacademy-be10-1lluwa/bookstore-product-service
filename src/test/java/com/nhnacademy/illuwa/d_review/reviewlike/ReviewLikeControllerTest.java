package com.nhnacademy.illuwa.d_review.reviewlike;

import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.reviewlike.controller.ReviewLikeController;
import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.service.Impl.ReviewLikeServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    private Book book;

    private Long reviewId;
    private Long memberId;
    private final LocalDateTime fixedTime = LocalDateTime.of(2099, 8, 22, 4, 17, 32);

    @BeforeEach
    void setUp() {
        String imageUrl = "https://media.tenor.com/qLET435-HUwAAAAi/dumb-pepe.gif";
        book = new Book(
                999999L,
                "이상한 책",
                "목차",
                "아무 설명",
                "이상한 사람",
                "무명출판",
                LocalDate.of(2024, 1, 1),
                "123456789X",
                BigDecimal.valueOf(100000L),
                BigDecimal.valueOf(99999L),
                List.of(new BookImage(book, imageUrl, ImageType.DETAIL)),
                new BookExtraInfo(Status.NORMAL,false,1),
                null,
                null
        );

        Review review = new Review(
                1L,
                "리뷰 제목",
                "리뷰 내용",
                5,
                fixedTime,
                book,
                9999L
        );

        reviewId = review.getReviewId();
        memberId = 7777L;
    }

    @Test
    @DisplayName("좋아요 - 추가")
    public void reviewLikeAddTest() throws Exception {
        ReviewLikeResponse response = new ReviewLikeResponse(true, 16L);

        given(reviewLikeService.toggleLike(eq(reviewId), eq(memberId))).willReturn(response);

        mockMvc.perform(post("/api/reviews/{review-id}/likes", reviewId)
                        .header("X-USER-ID", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likedByMe").value(true))
                .andExpect(jsonPath("$.likeCount").value(16));

        verify(reviewLikeService).toggleLike(eq(reviewId), eq(memberId));
    }

    @Test
    @DisplayName("좋아요 - 취소")
    public void reviewLikeCancelTest() throws Exception {
        ReviewLikeResponse response = new ReviewLikeResponse(false, 3L);

        given(reviewLikeService.toggleLike(eq(reviewId), eq(memberId))).willReturn(response);

        mockMvc.perform(post("/api/reviews/{review-id}/likes", reviewId)
                        .header("X-USER-ID", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likedByMe").value(false))
                .andExpect(jsonPath("$.likeCount").value(3));

        verify(reviewLikeService).toggleLike(eq(reviewId), eq(memberId));
    }

    @Test
    @DisplayName("리뷰 좋아요 수 가져오기 - 정상 조회")
    void getLikeCountsFromReviewsTest() throws Exception {
        List<Long> reviewIds = List.of(1L, 2L, 3L);
        Map<Long, Long> mockCounts = Map.of(
                1L, 10L,
                2L, 5L,
                3L, 0L
        );

        given(reviewLikeService.getLikeCountsFromReviews(reviewIds)).willReturn(mockCounts);

        mockMvc.perform(get("/api/public/reviews/likes")
                        .param("review-ids", "1", "2", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['1']").value(10))
                .andExpect(jsonPath("$.['2']").value(5))
                .andExpect(jsonPath("$.['3']").value(0));

        verify(reviewLikeService).getLikeCountsFromReviews(reviewIds);
    }

    @Test
    @DisplayName("좋아요 상태 가져오기 - 내가 좋아요 한 리뷰 ID 리스트 조회")
    void getMyLikedReviewsTest() throws Exception {
        List<Long> reviewIds = List.of(1L, 2L, 3L);
        List<Long> likedReviewIds = List.of(1L, 3L);

        given(reviewLikeService.getMyLikedReviews(reviewIds, memberId)).willReturn(likedReviewIds);

        mockMvc.perform(get("/api/reviews/likes/status")
                        .param("review-ids", "1", "2", "3")
                        .header("X-USER-ID", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(3))
                .andExpect(jsonPath("$", Matchers.hasSize(2)));

        verify(reviewLikeService).getMyLikedReviews(reviewIds, memberId);
    }
}

