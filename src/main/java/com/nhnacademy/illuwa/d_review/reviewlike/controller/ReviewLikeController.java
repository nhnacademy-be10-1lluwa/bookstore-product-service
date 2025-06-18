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

    // TODO: memberId 가져오는 방법 결정되면 수정

    @PostMapping
    public ResponseEntity<ReviewLikeResponse> addLike(@PathVariable Long bookId, @PathVariable Long reviewId) {
        Long memberId = null;

        return ResponseEntity.ok(reviewLikeService.addLike(bookId, reviewId, memberId));
    }

    @DeleteMapping
    public ResponseEntity<ReviewLikeResponse> cancelLike(@PathVariable Long bookId, @PathVariable Long reviewId) {
        Long memberId = null;

        return ResponseEntity.ok(reviewLikeService.cancelLike(reviewId, memberId));
    }

    @GetMapping
    public ResponseEntity<ReviewLikeResponse> getLikeInfo(@PathVariable Long bookId, @PathVariable Long reviewId) {
        Long memberId = null;

        return ResponseEntity.ok(reviewLikeService.getLikeInfo(reviewId, memberId));
    }
}
