package com.nhnacademy.illuwa.d_review.review.service;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface ReviewService {
    ReviewResponse createReview(Long bookId, Long memberId, ReviewRequest request) throws Exception;

    Page<ReviewResponse> getReviewPages(Long bookId, Pageable pageable, Long memberId);

    Page<ReviewResponse> getReviewPagesWithoutLogin(Long bookId, Pageable pageable);

    ReviewResponse getReviewDetails(Long bookId, Long reviewId, Long memberId);

    ReviewResponse updateReview(Long bookId, Long reviewId, Long memberId, ReviewRequest request) throws Exception;

    Map<Long, Boolean> areReviewsWritten(List<Long> bookIds, Long memberId);
}