package com.nhnacademy.illuwa.cart.exception;

public class BookDiscontinuedException extends RuntimeException {
    public BookDiscontinuedException(String message) {
        super(message);
    }
}
