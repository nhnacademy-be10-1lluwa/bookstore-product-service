package com.nhnacademy.illuwa.d_review.comment.dto;

import com.nhnacademy.illuwa.d_review.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String commentContents;
    private LocalDateTime commentDate;
    private Long reviewId;
    private Long memberId;

    static public CommentResponse from(Comment comment){
        return new CommentResponse(
                comment.getCommentId(),
                comment.getCommentContents(),
                comment.getCommentDate(),
                comment.getReview().getReviewId(),
                comment.getMemberId()
        );
    }
}
