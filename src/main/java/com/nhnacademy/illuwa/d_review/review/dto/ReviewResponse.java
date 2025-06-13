package com.nhnacademy.illuwa.d_review.review.dto;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private String reviewTitle;
    private String reviewContent;
    private String reviewImageUrl;
    private Integer reviewRating;
    private LocalDateTime reviewDate;
    private Long bookId;
    private Long memberId;

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getReviewTitle(),
                review.getReviewContent(),
                review.getReviewImageUrl(),
                review.getReviewRating(),
                review.getReviewDate(),
                review.getBook().getId(),
                review.getMemberId()
        );
    }
}
