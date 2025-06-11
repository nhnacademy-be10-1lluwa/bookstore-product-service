package com.nhnacademy.illuwa.d_review.review.controller;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewListResponse;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long bookId, @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(bookId, request));
    }

    @GetMapping
    public ResponseEntity<ReviewListResponse> getReviewList(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewList(bookId));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewDetail(@PathVariable Long bookId, @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewDetail(bookId, reviewId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long bookId, @PathVariable Long reviewId, @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(bookId, reviewId, request));
    }
}