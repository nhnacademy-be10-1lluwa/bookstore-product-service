package com.nhnacademy.illuwa.d_review.reviewlike.controller;

import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/reviews/{reviewId}/likes")
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @PostMapping
    public ResponseEntity<ReviewLikeResponse> toggleLike(@PathVariable Long bookId,
                                                         @PathVariable Long reviewId,
                                                         @RequestHeader("X-USER-ID") Long memberId) {

        return ResponseEntity.ok(reviewLikeService.toggleLike(bookId, reviewId, memberId));
    }
}
