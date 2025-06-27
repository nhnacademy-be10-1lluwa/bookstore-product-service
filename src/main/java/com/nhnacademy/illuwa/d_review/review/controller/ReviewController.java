package com.nhnacademy.illuwa.d_review.review.controller;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<ReviewResponse>> getReviewPages(@PathVariable Long bookId,
                                              @PageableDefault(size = 5, sort = "reviewDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(reviewService.getReviewPages(bookId, pageable));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long bookId,
                                                       @PathVariable Long reviewId,
                                                       @RequestBody ReviewRequest request,
                                                       @RequestHeader("X-USER-ID") Long memberId) {
        return ResponseEntity.ok(reviewService.updateReview(bookId, reviewId, request, memberId));
    }
}