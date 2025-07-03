package com.nhnacademy.illuwa.d_review.review.controller;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long bookId,
                                                       @RequestPart("review") ReviewRequest request,
                                                       @RequestHeader("X-USER-ID") Long memberId,
                                                       @RequestPart(name = "images", required = false) List<MultipartFile> images) throws Exception {

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(bookId, request, memberId, images));
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviewPages(@PathVariable Long bookId,
                                                               @RequestHeader("X-USER-ID") Long memberId,
                                                               @PageableDefault(size = 5, sort = "reviewDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(reviewService.getReviewPages(bookId, pageable, memberId));
    }

    @PutMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long bookId,
                                                       @PathVariable Long reviewId,
                                                       @RequestPart("review") ReviewRequest request,
                                                       @RequestHeader("X-USER-ID") Long memberId,
                                                       @RequestPart(name = "images", required = false) List<MultipartFile> images) throws Exception {

        return ResponseEntity.ok(reviewService.updateReview(bookId, reviewId, request, memberId, images));
    }
}