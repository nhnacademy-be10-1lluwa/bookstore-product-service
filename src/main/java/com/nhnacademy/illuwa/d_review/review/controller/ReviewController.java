package com.nhnacademy.illuwa.d_review.review.controller;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(value = "/api/book-reviews/{bookId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createReview(@PathVariable long bookId,
                                                       @RequestHeader("X-USER-ID") long memberId,
                                                       @ModelAttribute @Valid ReviewRequest request) throws Exception {

        ReviewResponse response = reviewService.createReview(bookId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/api/book-reviews/{bookId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviewPages(@PathVariable long bookId,
                                                               @PageableDefault(size = 5, sort = "reviewDate", direction = Sort.Direction.DESC) Pageable pageable) {

        //        if(memberId == null) {
//            Page<ReviewResponse> responsePage = reviewService.getReviewPagesWithoutLogin(bookId, pageable);
//            return ResponseEntity.ok(responsePage);
//        } else {
//            Page<ReviewResponse> responsePage = reviewService.getReviewPages(bookId, pageable, memberId);
//            return ResponseEntity.ok(responsePage);
//        }
        Page<ReviewResponse> responsePage = reviewService.getReviewPagesWithoutLogin(bookId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping(value = "/api/book-reviews/{bookId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewDetails(@PathVariable long bookId,
                                                           @PathVariable long reviewId,
                                                           @RequestHeader("X-USER-ID") long memberId) {
        ReviewResponse response = reviewService.getReviewDetails(bookId, reviewId, memberId);
        return ResponseEntity.ok(response);
    }

    // 프론트에서 feign 으로 수정요청 받으려면 어쩔수 없이 Post 써야함 (feign 은 patch 미지원)
    @PostMapping(value = "/api/book-reviews/{bookId}/reviews/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable long bookId,
                                                       @PathVariable long reviewId,
                                                       @RequestHeader("X-USER-ID") Long memberId,
                                                       @ModelAttribute @Valid ReviewRequest request) throws Exception {

        ReviewResponse response = reviewService.updateReview(bookId, reviewId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/book-reviews/reviews/check-batch")
    public Map<Long, Boolean> areReviewsWritten(@RequestBody List<Long> bookIds,
                                                @RequestHeader("X-USER-ID") Long memberId) {
        return reviewService.areReviewsWritten(bookIds, memberId);
    }
}