package com.nhnacademy.illuwa.d_review.review.repository;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findReviewsByBook_Id(Long bookId, Pageable pageable);
    Optional<Review> findByBook_IdAndReviewId(Long bookId, Long reviewId);
    Optional<Review> findByBook_IdAndMemberId(Long bookId, Long memberId);
}
