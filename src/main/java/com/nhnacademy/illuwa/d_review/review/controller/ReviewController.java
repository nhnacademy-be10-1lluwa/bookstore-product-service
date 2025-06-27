package com.nhnacademy.illuwa.d_review.review.controller;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long bookId,
                                                       @RequestBody ReviewRequest request,
                                                       @RequestHeader("X-USER-ID") Long memberId) {
        return ResponseEntity.ok(reviewService.createReview(bookId, request, memberId));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviewList(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewList(bookId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long bookId,
                                                       @PathVariable Long reviewId,
                                                       @RequestBody ReviewRequest request,
                                                       @RequestHeader("X-USER-ID") Long memberId) {
        return ResponseEntity.ok(reviewService.updateReview(bookId, reviewId, request, memberId));
    }
}