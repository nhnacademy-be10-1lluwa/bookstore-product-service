package com.nhnacademy.illuwa.d_review.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long reviewId) {
        super("리뷰를 찾을 수 없습니다. Review ID: " + reviewId);
    }
}