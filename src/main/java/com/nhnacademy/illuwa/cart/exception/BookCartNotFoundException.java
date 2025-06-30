package com.nhnacademy.illuwa.cart.exception;

public class BookCartNotFoundException extends RuntimeException {
    public BookCartNotFoundException(String message) {
        super(message);
    }
}
