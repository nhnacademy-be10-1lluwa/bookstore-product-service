package com.nhnacademy.illuwa.common.exception;

import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiParsingException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentNotFoundException;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentStatusInvalidException;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.d_review.reviewlike.exception.AlreadyLikedException;
import com.nhnacademy.illuwa.d_review.reviewlike.exception.CannotCancelLikeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Map<String, Object> body = new LinkedHashMap<>();
    private HttpStatus status;
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

    // TODO: 추후 수정
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFoundException(ReviewNotFoundException e) {
        log.error("{}", e.getMessage(), e);
        return ResponseEntity.status(ReviewNotFoundException.getStatusC0de()).body(e.getMessage());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Object> handleCommentNotFoundException(CommentNotFoundException e) {
        status = HttpStatus.NOT_FOUND;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Comment_Not_Found"); // <-- 중요: 클라이언트가 파싱할 고유 코드
        body.put("message", e.getMessage()); // 또는 고정 메시지 "요청한 사용자를 찾을 수 없습니다."

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(CommentStatusInvalidException.class)
    public ResponseEntity<Object> handleCommentStatusInvalidException(CommentStatusInvalidException e) {
        status = HttpStatus.CONFLICT;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Comment_Status_Invalid"); // <-- 중요: 클라이언트가 파싱할 고유 코드
        body.put("message", e.getMessage()); // 또는 고정 메시지 "요청한 사용자를 찾을 수 없습니다."

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<Object> handleAlreadyLikedException(AlreadyLikedException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Already_Liked"); // <-- 중요: 클라이언트가 파싱할 고유 코드
        body.put("message", e.getMessage()); // 또는 고정 메시지 "요청한 사용자를 찾을 수 없습니다."

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(CannotCancelLikeException.class)
    public ResponseEntity<Object> handleCannotCancelLikeException(CannotCancelLikeException e) {
        HttpStatus status = HttpStatus.CONFLICT;

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Cannot_Cancel_Like"); // <-- 중요: 클라이언트가 파싱할 고유 코드
        body.put("message", e.getMessage()); // 또는 고정 메시지 "요청한 사용자를 찾을 수 없습니다."

        return new ResponseEntity<>(body, status);
    }
}
