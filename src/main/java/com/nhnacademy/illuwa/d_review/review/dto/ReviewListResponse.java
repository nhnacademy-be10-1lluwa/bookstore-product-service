package com.nhnacademy.illuwa.d_review.review.dto;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {
    List<Review> reviews;
}
