package com.nhnacademy.illuwa.d_review.comment.exception;

public class InvalidCommentStatusException extends RuntimeException {
    public InvalidCommentStatusException(String message) {
        super(message);
    }
}