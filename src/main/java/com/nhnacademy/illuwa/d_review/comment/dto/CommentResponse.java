package com.nhnacademy.illuwa.d_review.comment.dto;

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
}
