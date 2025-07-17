package com.nhnacademy.illuwa.d_review.reviewlike.controller;

import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{review-id}/likes")
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @PostMapping
    public ResponseEntity<ReviewLikeResponse> toggleLike(@PathVariable(name = "review-id") Long reviewId,
                                                         @RequestHeader("X-USER-ID") Long memberId) {

        return ResponseEntity.ok(reviewLikeService.toggleLike(reviewId, memberId));
    }

    @GetMapping
    public ResponseEntity<ReviewLikeResponse> getLikeInfo(@PathVariable(name = "review-id") Long reviewId, Long memberId) {
        return ResponseEntity.ok(reviewLikeService.getLikeInfo(reviewId, memberId));
    }
}
