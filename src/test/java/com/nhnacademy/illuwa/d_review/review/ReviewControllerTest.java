package com.nhnacademy.illuwa.d_review.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_review.review.controller.ReviewController;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewListResponse;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    LocalDateTime fixedDate = LocalDateTime.now();
    ReviewRequest request;
    ReviewResponse response;
    ReviewListResponse listResponse;
    Long bookId = 10L;
    Long reviewId = 1L;
    Long memberId = 99L;

    @BeforeEach
    void setUp() {
        request = new ReviewRequest("리뷰리뷰리뷰", "포인트 냠냠", "/img/test.png", 4);
        response = new ReviewResponse(reviewId, "리뷰리뷰리뷰", "포인트 냠냠", "/img/test.png", 4, fixedDate, bookId, memberId);
        listResponse = new ReviewListResponse();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리뷰 작성")
    public void reviewCreateTest() throws Exception {
        given(reviewService.createReview(eq(bookId), any(ReviewRequest.class))).willReturn(response);
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

    }
}
