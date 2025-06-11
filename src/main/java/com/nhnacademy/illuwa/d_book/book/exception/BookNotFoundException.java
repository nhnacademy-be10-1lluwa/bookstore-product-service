package com.nhnacademy.illuwa.d_book.book.exception;

import com.nhnacademy.illuwa.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class BookNotFoundException extends CustomException {
    private static final String MESSAGE = "도서를 찾을 수 없습니다. ";
    public BookNotFoundException(Long bookId) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + "Book ID: " + bookId);
    }
}
