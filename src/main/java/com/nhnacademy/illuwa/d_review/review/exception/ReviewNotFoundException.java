package com.nhnacademy.illuwa.d_review.review.exception;

import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long reviewId) {
        super(makeMessage(reviewId));
    }

    public static String makeMessage(Long reviewId){
       return "StatusCode: " + HttpStatus.NOT_FOUND.value()
               + " 리뷰를 찾을 수 없습니다. Review ID: " + reviewId;
    }
}
