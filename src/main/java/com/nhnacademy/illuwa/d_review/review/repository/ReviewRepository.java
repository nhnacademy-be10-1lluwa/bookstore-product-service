package com.nhnacademy.illuwa.d_review.review.repository;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByBook_BookId(Long bookId);
}
