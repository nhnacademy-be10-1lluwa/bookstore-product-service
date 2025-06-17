package com.nhnacademy.illuwa.common.exception;

import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiParsingException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // TODO: 예외 추가될 떄마다 추가
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFoundException(ReviewNotFoundException e) {
        log.error("{}", e.getMessage(), e);
        return ResponseEntity.status(ReviewNotFoundException.getStatusC0de()).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundBookException.class)
    public ResponseEntity<String> handleNotFoundBookException(NotFoundBookException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExistsBookException(BookAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BookApiParsingException.class)
    public ResponseEntity<String> handleApiParsingException(BookApiParsingException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BookApiException.class)
    public ResponseEntity<String> handleAladinApiException(BookApiException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ex.getMessage());
    }

}
