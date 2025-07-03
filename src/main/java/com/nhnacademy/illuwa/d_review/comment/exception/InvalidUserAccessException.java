package com.nhnacademy.illuwa.d_review.comment.exception;

public class InvalidUserAccessException extends RuntimeException {
    public InvalidUserAccessException(String message) {
        super(message);
    }
}
