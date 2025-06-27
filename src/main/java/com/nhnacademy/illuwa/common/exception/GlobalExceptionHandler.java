package com.nhnacademy.illuwa.common.exception;

import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiParsingException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentNotFoundException;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentStatusInvalidException;
import com.nhnacademy.illuwa.d_review.review.exception.MemberIdDoesNotMatchWithReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
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

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Object> handleReviewNotFoundException(ReviewNotFoundException e) {
        status = HttpStatus.NOT_FOUND;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Review_Not_Found"); // <-- 중요: 클라이언트가 파싱할 고유 코드
        body.put("message", e.getMessage()); // 또는 고정 메시지 "요청한 사용자를 찾을 수 없습니다."

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(MemberIdDoesNotMatchWithReviewException.class)
    public ResponseEntity<Object> handleMemberIdDoesNotMatchWithReviewException(MemberIdDoesNotMatchWithReviewException e) {
        status = HttpStatus.FORBIDDEN;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Member_Id_Does_Not_Match"); // <-- 중요: 클라이언트가 파싱할 고유 코드
        body.put("message", e.getMessage()); // 또는 고정 메시지 "요청한 사용자를 찾을 수 없습니다."

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Object> handleCommentNotFoundException(CommentNotFoundException e) {
        status = HttpStatus.NOT_FOUND;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Comment_Not_Found"); // <-- 중요: 클라이언트가 파싱할 고유 코드
        body.put("message", e.getMessage()); // 또는 고정 메시지 "요청한 사용자를 찾을 수 없습니다."

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

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

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }
}
