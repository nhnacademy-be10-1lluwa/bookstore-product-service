package com.nhnacademy.illuwa.d_review.review.controller;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-reviews/{bookId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createReview(@PathVariable long bookId,
                                                       @RequestHeader("X-USER-ID") long memberId,
                                                       @ModelAttribute @Valid ReviewRequest request) throws Exception {

        ReviewResponse response = reviewService.createReview(bookId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviewPages(@PathVariable long bookId,
                                                               @RequestHeader("X-USER-ID") long memberId,
                                                               @PageableDefault(size = 5, sort = "reviewDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewResponse> responsePage = reviewService.getReviewPages(bookId, pageable, memberId);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping(value = "/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewDetails(@PathVariable long bookId,
                                                           @PathVariable long reviewId,
                                                           @RequestHeader("X-USER-ID") long memberId) {
        ReviewResponse response = reviewService.getReviewDetails(bookId, reviewId, memberId);
        return ResponseEntity.ok(response);
    }

    // 프론트에서 feign 으로 수정요청 받으려면 어쩔수 없이 Post 써야함 (feign 은 patch 미지원)
    @PostMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable long bookId,
                                                       @PathVariable long reviewId,
                                                       @RequestHeader("X-USER-ID") Long memberId,
                                                       @ModelAttribute @Valid ReviewRequest request) throws Exception {

        ReviewResponse response = reviewService.updateReview(bookId, reviewId, memberId, request);
        return ResponseEntity.ok(response);
    }
}