package com.nhnacademy.illuwa.common.exception;

import com.nhnacademy.illuwa.cart.exception.BookCartNotFoundException;
import com.nhnacademy.illuwa.cart.exception.InsufficientStockException;
import com.nhnacademy.illuwa.cart.exception.NotFoundMemberIdException;
import com.nhnacademy.illuwa.common.dto.ErrorResponse;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiException;
import com.nhnacademy.illuwa.d_book.book.exception.BookApiParsingException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_review.review.exception.CannotWriteReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.MemberIdDoesNotMatchWithReviewException;
import com.nhnacademy.illuwa.d_review.review.exception.ReviewNotFoundException;
import com.nhnacademy.illuwa.infra.storage.exception.FileUploadFailedException;
import com.nhnacademy.illuwa.infra.storage.exception.InvalidFileFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileFormatException(InvalidFileFormatException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase()
                ,"INVALID_FILE_FORMAT",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadFailedException(FileUploadFailedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase()
                ,"FILE_UPLOAD_FAILED",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("MinIO에 업로드 중 에러가 발생했습니다.", e);
        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(NotFoundBookException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundBookException(NotFoundBookException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase()
                ,"BOOK_NOT_FOUND",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsBookException(BookAlreadyExistsException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "DUPLICATE_BOOK",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(BookApiParsingException.class)
    public ResponseEntity<ErrorResponse> handleApiParsingException(BookApiParsingException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "API_PARSING_ERROR",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(BookApiException.class)
    public ResponseEntity<ErrorResponse> handleAladinApiException(BookApiException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "ALADIN_API_ERROR",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFoundException(ReviewNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase()
                ,"REVIEW_NOT_FOUND",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(CannotWriteReviewException.class)
    public ResponseEntity<ErrorResponse> handleCannotWriteReviewException(CannotWriteReviewException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase()
                ,"CANNOT_WRITE_REVIEW",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MemberIdDoesNotMatchWithReviewException.class)
    public ResponseEntity<ErrorResponse> handleMemberIdDoesNotMatchWithReviewException(MemberIdDoesNotMatchWithReviewException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase()
                ,"MEMBERID_DOES_NOT_MATCH",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "INSUFFICIENT_STOCK",
                e.getMessage(),
                request.getRequestURI()
        );

        log.error("에러코드: {}, 메시지: {}", status.value(), e.getMessage(), e);

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(NotFoundMemberIdException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundMemberIdException(NotFoundMemberIdException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "MEMBER_NOT_FOUND",
                e.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(BookCartNotFoundException.class)
    public ResponseEntity<Object> handleBookCartNotFoundException(BookCartNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "BOOKCART_NOT_FOUND",
                e.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, status);
    }
}