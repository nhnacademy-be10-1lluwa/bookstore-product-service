package com.nhnacademy.illuwa.d_review.comment.exception;

public class CommentStatusInvalidException extends RuntimeException {
    public CommentStatusInvalidException(Long reviewId, Long commentId) {
        super("리뷰와 댓글이 일치하지 않습니다. Review ID: " + reviewId + "Comment ID: " + commentId);
    }
}