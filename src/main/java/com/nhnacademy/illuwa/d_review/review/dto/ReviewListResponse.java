package com.nhnacademy.illuwa.d_review.review.dto;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {
    List<Review> reviews;
}
