package com.nhnacademy.illuwa.d_review.reviewlike.exception;

public class CannotCancelLikeException extends RuntimeException {
    public CannotCancelLikeException() {
        super("이미 좋아요가 해제되어 있습니다.");
    }
}