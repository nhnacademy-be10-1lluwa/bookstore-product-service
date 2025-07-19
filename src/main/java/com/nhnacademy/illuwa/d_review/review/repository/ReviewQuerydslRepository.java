package com.nhnacademy.illuwa.d_review.review.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReviewQuerydslRepository {
    Map<Long, String> findBookTitleMapByReviewIds(List<Long> reviewIds);
}
