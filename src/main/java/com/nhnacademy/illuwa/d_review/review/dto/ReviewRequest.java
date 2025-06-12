package com.nhnacademy.illuwa.d_review.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
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
}
