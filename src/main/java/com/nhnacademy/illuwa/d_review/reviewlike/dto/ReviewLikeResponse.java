package com.nhnacademy.illuwa.d_review.reviewlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewLikeResponse {
    private boolean likedByMe;
    private Long likeCount;
}
