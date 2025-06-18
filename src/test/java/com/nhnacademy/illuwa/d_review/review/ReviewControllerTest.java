package com.nhnacademy.illuwa.d_review.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_review.review.controller.ReviewController;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponseList;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    private final Long bookId = 10L;
    private final Long reviewId = 1L;
    private final Long memberId = 99L;
    private final LocalDateTime fixedDate = LocalDateTime.of(2024, 2, 19, 21, 32, 11);

    private ReviewRequest request;
    private ReviewResponse response;

    @BeforeEach
    void setUp() {
        request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", "/img/test.png", 4);
        response = new ReviewResponse(reviewId, "리뷰리뷰리뷰", "포인트 냠냠", "/img/test.png", 4, fixedDate, bookId, memberId);
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리뷰 작성")
    public void reviewCreateTest() throws Exception {
        // given
        given(reviewService.createReview(eq(bookId), any(ReviewRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/books/{bookId}/reviews", bookId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.reviewTitle").value("리뷰리뷰리뷰"))
                .andExpect(jsonPath("$.reviewContent").value("포인트 냠냠"))
                .andExpect(jsonPath("$.reviewImageUrl").value("/img/test.png"))
                .andExpect(jsonPath("$.reviewRating").value(4))
                .andExpect(jsonPath("$.reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.bookId").value(bookId))
                .andExpect(jsonPath("$.memberId").value(memberId));
        verify(reviewService).createReview(eq(bookId), any(ReviewRequest.class));
    }

    @Test
    @DisplayName("리뷰 목록 조회")
    public void reviewListLoadTest() throws Exception {
        // 리뷰 목록 설정
        Long reviewId2 = 2L;
        Long memberId2 = 22L;

        ReviewResponse response2 = new ReviewResponse(reviewId2, "대충 리뷰 제목", "대충 내용", "/img/test.png", 3, fixedDate, bookId, memberId2);
        List<ReviewResponse> reviewResponseList = new ArrayList<>();
        reviewResponseList.add(response);
        reviewResponseList.add(response2);
        ReviewResponseList reviewListResponse = new ReviewResponseList(reviewResponseList);

        // given
        given(reviewService.getReviewList(eq(bookId))).willReturn(reviewListResponse);

        // when & then
        mockMvc.perform(get("/books/{bookId}/reviews", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].reviewId").value(reviewId))
                .andExpect(jsonPath("$.reviews[0].reviewTitle").value("리뷰리뷰리뷰"))
                .andExpect(jsonPath("$.reviews[0].reviewContent").value("포인트 냠냠"))
                .andExpect(jsonPath("$.reviews[0].reviewImageUrl").value("/img/test.png"))
                .andExpect(jsonPath("$.reviews[0].reviewRating").value(4))
                .andExpect(jsonPath("$.reviews[0].reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.reviews[0].bookId").value(bookId))
                .andExpect(jsonPath("$.reviews[0].memberId").value(memberId))
                .andExpect(jsonPath("$.reviews[1].reviewId").value(reviewId2))
                .andExpect(jsonPath("$.reviews[1].reviewTitle").value("대충 리뷰 제목"))
                .andExpect(jsonPath("$.reviews[1].reviewContent").value("대충 내용"))
                .andExpect(jsonPath("$.reviews[1].reviewImageUrl").value("/img/test.png"))
                .andExpect(jsonPath("$.reviews[1].reviewRating").value(3))
                .andExpect(jsonPath("$.reviews[1].reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.reviews[1].bookId").value(bookId))
                .andExpect(jsonPath("$.reviews[1].memberId").value(memberId2));

        verify(reviewService).getReviewList(eq(bookId));
    }

    @Test
    @DisplayName("리뷰 상세 가져오기 테스트")
    void reviewDetailLoadTest() throws Exception {
        // given
        given(reviewService.getReviewDetail(bookId, reviewId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/books/{bookId}/reviews/{reviewId}", bookId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.reviewTitle").value("리뷰리뷰리뷰"))
                .andExpect(jsonPath("$.reviewContent").value("포인트 냠냠"))
                .andExpect(jsonPath("$.reviewImageUrl").value("/img/test.png"))
                .andExpect(jsonPath("$.reviewRating").value(4))
                .andExpect(jsonPath("$.reviewDate").value(fixedDate.toString()))
                .andExpect(jsonPath("$.bookId").value(bookId))
                .andExpect(jsonPath("$.memberId").value(memberId));

        verify(reviewService).getReviewDetail(bookId, reviewId);
    }

    @Test
    @DisplayName("리뷰 수정 테스트")
    public void reviewUpdateTest() throws Exception {
        LocalDateTime fixedDate2 = LocalDateTime.of(2024, 3, 16, 12, 28, 43);
        ReviewRequest request2 = new ReviewRequest("대충 리뷰 수정함", "대충 내용 수정함", "/img/test2.png", 5);
        ReviewResponse response2 = new ReviewResponse(reviewId, "대충 리뷰 수정함", "대충 내용 수정함", "/img/test2.png", 5, fixedDate2, bookId, memberId);

        // given
        given(reviewService.updateReview(eq(bookId), eq(reviewId), any(ReviewRequest.class))).willReturn(response2);

        // when & then
        mockMvc.perform(put("/books/{bookId}/reviews/{reviewId}", bookId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.reviewTitle").value("대충 리뷰 수정함"))
                .andExpect(jsonPath("$.reviewContent").value("대충 내용 수정함"))
                .andExpect(jsonPath("$.reviewImageUrl").value("/img/test2.png"))
                .andExpect(jsonPath("$.reviewRating").value(5))
                .andExpect(jsonPath("$.reviewDate").value(fixedDate2.toString()))
                .andExpect(jsonPath("$.bookId").value(bookId))
                .andExpect(jsonPath("$.memberId").value(memberId));

        verify(reviewService).updateReview(eq(bookId), eq(reviewId), any(ReviewRequest.class));
    }
}
