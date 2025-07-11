package com.nhnacademy.illuwa.common.exception;

import com.nhnacademy.illuwa.cart.exception.BookCartNotFoundException;
import com.nhnacademy.illuwa.cart.exception.InsufficientStockException;
import com.nhnacademy.illuwa.cart.exception.NotFoundMemberIdException;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiParsingException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_review.comment.exception.CommentNotFoundException;
import com.nhnacademy.illuwa.d_review.comment.exception.InvalidCommentStatusException;
import com.nhnacademy.illuwa.d_review.review.exception.CannotWriteReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.MemberIdDoesNotMatchWithReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.infra.storage.exception.FileUploadFailedException;
import com.nhnacademy.illuwa.infra.storage.exception.InvalidFileFormatException;
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

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<Object> handleInvalidFileFormatException(InvalidFileFormatException e) {
        status = HttpStatus.BAD_REQUEST;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Invalid_File_Format");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<Object> handleFileUploadFailedException(FileUploadFailedException e) {
        status = HttpStatus.INTERNAL_SERVER_ERROR;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "File_Upload_Failed");
        body.put("message", e.getMessage());

        log.error("MinIO에 업로드 중 에러가 발생했습니다.", e);
        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
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

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Object> handleReviewNotFoundException(ReviewNotFoundException e) {
        status = HttpStatus.NOT_FOUND;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Review_Not_Found");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(CannotWriteReviewException.class)
    public ResponseEntity<Object> handleCannotWriteReviewException(CannotWriteReviewException e) {
        status = HttpStatus.NOT_FOUND;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Review_Not_Found");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(MemberIdDoesNotMatchWithReviewException.class)
    public ResponseEntity<Object> handleMemberIdDoesNotMatchWithReviewException(MemberIdDoesNotMatchWithReviewException e) {
        status = HttpStatus.FORBIDDEN;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Member_Id_Does_Not_Match");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Object> handleCommentNotFoundException(CommentNotFoundException e) {
        status = HttpStatus.NOT_FOUND;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Comment_Not_Found");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(InvalidCommentStatusException.class)
    public ResponseEntity<Object> handleCommentStatusInvalidException(InvalidCommentStatusException e) {
        status = HttpStatus.CONFLICT;

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Comment_Status_Invalid");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Object> handleInsufficientStockException(InsufficientStockException e) {
        status = HttpStatus.CONFLICT;
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Insufficient_Stock");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(NotFoundMemberIdException.class)
    public ResponseEntity<Object> handleNotFoundMemberIdException(NotFoundMemberIdException e) {
        status = HttpStatus.NOT_FOUND;
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Member_Not_Found");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(BookCartNotFoundException.class)
    public ResponseEntity<Object> handleBookCartNotFoundException(BookCartNotFoundException e) {
        status = HttpStatus.NOT_FOUND;
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", "Book_Cart_Not_Found");
        body.put("message", e.getMessage());

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(body, status);
    }
}
