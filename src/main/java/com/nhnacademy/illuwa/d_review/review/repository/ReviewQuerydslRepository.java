package com.nhnacademy.illuwa.d_review.review.repository;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

@Repository
public interface ReviewQuerydslRepository {
    Map<Long, String> findBookTitleMapByReviewIds(Collection<Long> reviewIds);
}
