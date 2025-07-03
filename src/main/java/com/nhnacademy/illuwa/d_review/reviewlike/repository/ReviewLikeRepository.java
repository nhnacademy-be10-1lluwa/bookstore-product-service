package com.nhnacademy.illuwa.d_review.reviewlike.repository;

import com.nhnacademy.illuwa.d_review.reviewlike.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long>, ReviewLikeQuerydslRepository {
    boolean existsByReview_ReviewIdAndMemberId(Long reviewId, Long memberId);

    long countByReview_ReviewId(Long reviewId);

    void deleteByReview_ReviewIdAndMemberId(Long reviewId, Long memberId);
}