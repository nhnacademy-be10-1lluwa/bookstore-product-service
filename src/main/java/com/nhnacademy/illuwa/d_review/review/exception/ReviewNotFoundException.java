package com.nhnacademy.illuwa.d_review.review.exception;

import org.springframework.http.HttpStatus;

// TODO: 예외처리 방식 결정되면 해당 클래스로 extends 변경
public class ReviewNotFoundException extends RuntimeException {
//    private static final String MESSAGE = "리뷰를 찾을 수 없습니다. ";
//    public ReviewNotFoundException(Long reviewId) {
//        super(HttpStatus.NOT_FOUND.value(), MESSAGE + "Review ID: " + reviewId);
//    }
}
