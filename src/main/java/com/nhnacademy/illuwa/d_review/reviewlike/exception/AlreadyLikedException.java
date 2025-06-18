package com.nhnacademy.illuwa.d_review.reviewlike.exception;

public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException() {
        super("좋아요를 여러 번 설정 할 수 없습니다.");
    }
}