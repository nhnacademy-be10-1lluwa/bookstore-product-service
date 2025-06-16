package com.nhnacademy.illuwa.d_book.book.exception;

import org.springframework.http.HttpStatus;

public class BookApiException extends RuntimeException {
    private final HttpStatus status;

    public BookApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public  HttpStatus getStatus(){
        return status;
    }
}
