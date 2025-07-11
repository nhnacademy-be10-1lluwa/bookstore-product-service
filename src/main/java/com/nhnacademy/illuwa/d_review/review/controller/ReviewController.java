package com.nhnacademy.illuwa.d_review.review.controller;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/books/{bookId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long bookId,
                                                       @RequestPart("review") @Valid ReviewRequest request,
                                                       @RequestHeader("X-USER-ID") Long memberId,
                                                       @RequestPart(name = "images", required = false) List<MultipartFile> images) throws Exception {

        ReviewResponse response = reviewService.createReview(bookId, request, memberId, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviewPages(@PathVariable Long bookId,
                                                               @RequestHeader("X-USER-ID") Long memberId,
                                                               @PageableDefault(size = 5, sort = "reviewDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewResponse> responsePage = reviewService.getReviewPages(bookId, pageable, memberId);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping(value = "/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewDetails(@PathVariable Long bookId,
                                                           @PathVariable Long reviewId,
                                                           @RequestHeader("X-USER-ID") Long memberId) {
        ReviewResponse response = reviewService.getReviewDetails(bookId, reviewId, memberId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long bookId,
                                                       @PathVariable Long reviewId,
                                                       @RequestPart("review") @Valid ReviewRequest request,
                                                       @RequestHeader("X-USER-ID") Long memberId,
                                                       @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                                       @RequestPart(name = "keepImageUrls", required = false) List<String> keepImageUrls) throws Exception {

        ReviewResponse response = reviewService.updateReview(bookId, reviewId, request, memberId, images, keepImageUrls);
        return ResponseEntity.ok(response);
    }
}