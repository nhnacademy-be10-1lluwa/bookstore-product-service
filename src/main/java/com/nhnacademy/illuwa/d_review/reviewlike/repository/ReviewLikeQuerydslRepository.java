package com.nhnacademy.illuwa.d_review.reviewlike.repository;

import java.util.List;
import java.util.Map;

public interface ReviewLikeQuerydslRepository {
    List<Long> findMyLikedReviewIds(List<Long> reviewIds, Long memberId);

    Map<Long, Long> countLikesByReviewIds(List<Long> reviewIds);
}
