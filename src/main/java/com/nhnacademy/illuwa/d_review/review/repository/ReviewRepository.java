package com.nhnacademy.illuwa.d_review.review.repository;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByBook_Id(Long bookId);
    Optional<Review> findByBook_IdAndReviewId(Long bookId, Long reviewId);
}
