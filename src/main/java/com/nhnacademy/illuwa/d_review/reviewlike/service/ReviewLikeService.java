package com.nhnacademy.illuwa.d_review.reviewlike.service;

import com.nhnacademy.illuwa.d_review.reviewlike.dto.ReviewLikeResponse;

import java.util.List;
import java.util.Map;

public interface ReviewLikeService {
    ReviewLikeResponse toggleLike(Long reviewId, Long memberId);

    Map<Long, Long> getLikeCountsFromReviews(List<Long> reviewIds);

    List<Long> getMyLikedReviews(List<Long> reviewIds, Long memberId);

//    ReviewLikeResponse getLikeInfo(Long reviewId, Long memberId);

    boolean isLikedByMe(Long reviewId, Long memberId);

    long getLikeCount(Long reviewId);
}
