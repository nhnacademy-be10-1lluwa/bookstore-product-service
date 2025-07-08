package com.nhnacademy.illuwa.d_review.comment.service;

import com.nhnacademy.illuwa.d_review.comment.dto.CommentRequest;
import com.nhnacademy.illuwa.d_review.comment.dto.CommentResponse;

import java.util.List;


public interface CommentService {
    CommentResponse createComment(Long reviewId, CommentRequest request, Long memberId);

    List<CommentResponse> getCommentList(Long reviewId);

    CommentResponse updateComment(Long reviewId, Long commentId, CommentRequest request, Long memberId);
}
