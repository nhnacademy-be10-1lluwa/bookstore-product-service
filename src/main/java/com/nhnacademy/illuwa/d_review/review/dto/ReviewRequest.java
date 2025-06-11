package com.nhnacademy.illuwa.d_review.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private String reviewTitle;
    private String reviewContent;
    private String reviewImageUrl;
    private Integer reviewRating;
    private LocalDateTime reviewDate;

    public ReviewRequest(String reviewTitle, String reviewContent, String reviewImageUrl, Integer reviewRating) {
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
        this.reviewImageUrl = reviewImageUrl;
        this.reviewRating = reviewRating;
        this.reviewDate = LocalDateTime.now();
    }
}
