package com.nhnacademy.illuwa.d_review.reviewlike.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReviewLikeQuerydslRepository {
    List<Long> findMyLikedReviewIds(List<Long> reviewIds, Long memberId);

    Map<Long, Long> countLikesByReviewIds(List<Long> reviewIds);
}
