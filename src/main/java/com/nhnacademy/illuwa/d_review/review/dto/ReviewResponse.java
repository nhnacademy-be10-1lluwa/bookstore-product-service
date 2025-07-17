package com.nhnacademy.illuwa.d_review.review.dto;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private String reviewTitle;
    private String reviewContent;
    private Integer reviewRating;
    private LocalDateTime reviewDate;
    private Long bookId;
    private Long memberId;
    private List<String> reviewImageUrls;

    public static ReviewResponse from(Review review, List<String> imageUrls) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getReviewTitle(),
                review.getReviewContent(),
                review.getReviewRating(),
                review.getReviewDate(),
                review.getBook().getId(),
                review.getMemberId(),
                imageUrls
        );
    }
}
