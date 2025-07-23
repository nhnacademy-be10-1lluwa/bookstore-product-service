package com.nhnacademy.illuwa.d_review.reviewlike.controller;

import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;
import com.nhnacademy.illuwa.d_review.reviewlike.service.ReviewLikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @Operation(summary = "리뷰 좋아요 설정 (토글)", description = "리뷰에 좋아요하거나 취소할 수 있습니다.")
    @PostMapping("/api/reviews/{review-id}/likes")
    public ResponseEntity<ReviewLikeResponse> toggleLike(@PathVariable(name = "review-id") Long reviewId,
                                                         @RequestHeader("X-USER-ID") Long memberId) {

        return ResponseEntity.ok(reviewLikeService.toggleLike(reviewId, memberId));
    }

    @Operation(summary = "리뷰 좋아요 수 가져오기", description = "리뷰 좋아요의 합계를 가져옵니다.")
    @GetMapping("/api/public/reviews/likes")
    public ResponseEntity<Map<Long, Long>> getLikeCountsFromReviews(@RequestParam(name = "review-ids") List<Long> reviewIds) {
        return ResponseEntity.ok(reviewLikeService.getLikeCountsFromReviews(reviewIds));
    }

    @Operation(summary = "좋아요 상태 가져오기", description = "자신이 리뷰에 좋아요 한 기록을 가져옵니다.")
    @GetMapping("/api/reviews/likes/status")
    public ResponseEntity<List<Long>> getMyLikedReviews(@RequestParam(name = "review-ids") List<Long> reviewIds, @RequestHeader("X-USER-ID") Long memberId) {
        return ResponseEntity.ok(reviewLikeService.getMyLikedReviews(reviewIds, memberId));
    }
}
