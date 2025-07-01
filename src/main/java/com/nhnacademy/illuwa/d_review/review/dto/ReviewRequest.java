package com.nhnacademy.illuwa.d_review.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private String reviewTitle;
    private String reviewContent;
    private Integer reviewRating;
    private List<String> reviewImageUrls;
}
