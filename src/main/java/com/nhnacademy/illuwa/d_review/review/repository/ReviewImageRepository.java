package com.nhnacademy.illuwa.d_review.review.repository;

import com.nhnacademy.illuwa.d_review.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findAllByReview_ReviewId(Long reviewId);
}
