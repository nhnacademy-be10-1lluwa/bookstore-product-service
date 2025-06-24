package com.nhnacademy.illuwa.d_review.comment.service;

import com.nhnacademy.illuwa.d_review.comment.dto.CommentRequest;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentResponse;
import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentNotFoundException;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentStatusInvalidException;
import com.nhnacademy.illuwa.d_review.comment.repository.CommentRepository;
import com.nhnacademy.illuwa.d_review.review.entity.Review;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse createComment(Long reviewId, CommentRequest request) {
        // TODO: memberId 가져오는 방식 확정되면 변경
        Long memberId = 7777L;

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));

        Comment comment = Comment.of(
                request.getCommentContents(),
                LocalDateTime.now(),
                review,
                memberId
        );

        Comment saved = commentRepository.save(comment);

        return CommentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentList(Long reviewId) {
        List<Comment> comments = commentRepository.findByReview_ReviewId(reviewId);

        return comments.stream().map(CommentResponse::from).toList();
    }

    @Transactional
    public CommentResponse updateComment(Long reviewId, Long commentId, CommentRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));

        if(!comment.getReview().getReviewId().equals(review.getReviewId())) {
            throw new CommentStatusInvalidException(reviewId, commentId);
        }

        // 영속 상태인 엔티티 수정하면 자동으로 update 반영됨
        comment.update(request.getCommentContents());

        return CommentResponse.from(comment);
    }
}
