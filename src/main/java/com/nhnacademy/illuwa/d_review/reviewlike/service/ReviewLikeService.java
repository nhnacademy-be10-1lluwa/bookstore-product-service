package com.nhnacademy.illuwa.d_review.reviewlike.service;

import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;

public interface ReviewLikeService {
    ReviewLikeResponse addLike(Long bookId, Long reviewId, Long memberId);

    ReviewLikeResponse cancelLike(Long reviewId, Long memberId);

    ReviewLikeResponse getLikeInfo(Long reviewId, Long memberId);

    boolean isLikedByMe(Long reviewId, Long memberId);

    long getLikeCount(Long reviewId);
}
