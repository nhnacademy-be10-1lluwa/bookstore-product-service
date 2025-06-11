package com.nhnacademy.illuwa.d_review.review.dto;

import com.nhnacademy.illuwa.d_review.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {
    List<Review> reviews;
}
