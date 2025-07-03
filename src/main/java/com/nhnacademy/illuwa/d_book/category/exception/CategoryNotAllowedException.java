package com.nhnacademy.illuwa.d_book.category.exception;

public class CategoryNotAllowedException extends RuntimeException {
    public CategoryNotAllowedException(String message) {
        super(message);
    }
}
