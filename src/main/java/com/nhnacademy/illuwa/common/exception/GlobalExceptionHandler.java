package com.nhnacademy.illuwa.common.exception;

import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import lombok.extern.slf4j.Slf4j;
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
}
