package com.nhnacademy.illuwa.d_review.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String commentContents;
    private Long reviewId;
    private Long memberId;
}
