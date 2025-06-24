package com.nhnacademy.illuwa.d_review.review.exception;

import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends RuntimeException {
    private static final int STATUS_CODE = HttpStatus.NOT_FOUND.value();

    public ReviewNotFoundException(Long reviewId) {
        super(makeMessage(reviewId));
    }

    public static String makeMessage(Long reviewId){
        return "StatusCode: " + STATUS_CODE
                + " 리뷰를 찾을 수 없습니다. Review ID: " + reviewId;
    }

    // static 이라 @Getter 안되서 만듬, 경고 무시하려고 o -> 0
    public static int getStatusC0de(){
        return STATUS_CODE;
    }
}