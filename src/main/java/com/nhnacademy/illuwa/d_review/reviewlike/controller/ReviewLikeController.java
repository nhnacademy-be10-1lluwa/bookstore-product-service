package com.nhnacademy.illuwa.d_review.reviewlike.controller;

import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @PostMapping("/api/reviews/{review-id}/likes")
    public ResponseEntity<ReviewLikeResponse> toggleLike(@PathVariable(name = "review-id") Long reviewId,
                                                         @RequestHeader("X-USER-ID") Long memberId) {

        return ResponseEntity.ok(reviewLikeService.toggleLike(reviewId, memberId));
    }

    @GetMapping("/api/reviews/likes")
    public ResponseEntity<Map<Long, Long>> getLikeCountsFromReviews(@RequestParam List<Long> reviewIds) {
        return ResponseEntity.ok(reviewLikeService.getLikeCountsFromReviews(reviewIds));
    }

    @GetMapping("/api/reviews/likes/status")
    public ResponseEntity<List<Long>> getMyLikedReviews(@RequestParam List<Long> reviewIds, @RequestHeader("X-USER-ID") Long memberId) {
        return ResponseEntity.ok(reviewLikeService.getMyLikedReviews(reviewIds, memberId));
    }
}
