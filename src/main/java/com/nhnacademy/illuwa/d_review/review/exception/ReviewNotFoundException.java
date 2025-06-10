package com.nhnacademy.illuwa.d_review.review.exception;

import com.nhnacademy.illuwa.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends ApiException {
    private static final String MESSAGE = "리뷰를 찾을 수 없습니다. ";
    public ReviewNotFoundException(Long reviewId) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + "Review ID: " + reviewId);
    }
}
