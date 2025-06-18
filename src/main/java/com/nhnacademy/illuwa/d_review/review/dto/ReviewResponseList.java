package com.nhnacademy.illuwa.d_review.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewResponseList {
    List<ReviewResponse> reviews;
}
