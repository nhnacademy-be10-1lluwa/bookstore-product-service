package com.nhnacademy.illuwa.d_review.comment.service.Impl;

import com.nhnacademy.illuwa.d_review.comment.dto.CommentRequest;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentResponse;
import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentNotFoundException;
import com.nhnacademy.illuwa.d_review.comment.exception.InvalidCommentStatusException;
import com.nhnacademy.illuwa.d_review.comment.exception.InvalidUserAccessException;
import com.nhnacademy.illuwa.d_review.comment.repository.CommentRepository;
import com.nhnacademy.illuwa.d_review.comment.service.CommentService;
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
public class CommentServiceImpl implements CommentService {
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentResponse createComment(Long reviewId, CommentRequest request, Long memberId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다. Review ID: " + reviewId));

        Comment comment = Comment.of(
                request.getCommentContents(),
                LocalDateTime.now(),
                review,
                memberId
        );

        Comment saved = commentRepository.save(comment);

        return CommentResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentList(Long reviewId) {
        List<Comment> comments = commentRepository.findByReview_ReviewId(reviewId);

        return comments.stream().map(CommentResponse::from).toList();
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long reviewId, Long commentId, CommentRequest request, Long memberId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다. Review ID: " + reviewId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다. Comment ID: " + commentId));

        if(!comment.getReview().getReviewId().equals(review.getReviewId())) {
            throw new InvalidCommentStatusException("리뷰와 댓글이 일치하지 않습니다. Review ID: " + reviewId + "Comment ID: " + commentId);
        }

        if(!comment.getMemberId().equals(memberId)) {
            throw new InvalidUserAccessException("댓글 작성자가 아닙니다!! 작성지: " + comment.getMemberId() + "현재 사용자: " + memberId);
        }

        // 영속 상태인 엔티티 수정하면 자동으로 update 반영됨
        comment.update(request.getCommentContents());

        return CommentResponse.from(comment);
    }
}
