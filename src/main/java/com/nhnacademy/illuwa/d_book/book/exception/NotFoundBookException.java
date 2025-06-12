package com.nhnacademy.illuwa.d_book.book.exception;

public class NotFoundBookException extends RuntimeException {
    public NotFoundBookException(String message) {
        super(message);
    }
}
