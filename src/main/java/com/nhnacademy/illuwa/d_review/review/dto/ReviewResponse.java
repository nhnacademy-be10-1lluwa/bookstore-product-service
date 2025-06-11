package com.nhnacademy.illuwa.d_review.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
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
}
