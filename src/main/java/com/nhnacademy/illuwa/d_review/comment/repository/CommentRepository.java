package com.nhnacademy.illuwa.d_review.comment.repository;

import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findByReview_ReviewId(Long reviewId);
}
