package com.nhnacademy.illuwa.d_review.reviewlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeResponse {
    boolean likedByMe;
    Long likeCount;

    public static ReviewLikeResponse from(boolean likedByMe, long likeCount) {
        return new ReviewLikeResponse(likedByMe, likeCount);
    }
}
