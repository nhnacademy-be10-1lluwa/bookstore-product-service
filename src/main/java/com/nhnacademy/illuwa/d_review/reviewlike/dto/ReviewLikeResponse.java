package com.nhnacademy.illuwa.d_review.reviewlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeResponse {
    Long likeId;
    boolean likedByMe;
    Long likeCount;
}
