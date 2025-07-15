package com.nhnacademy.illuwa.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;       // HTTP 상태 코드 (e.g., 400, 404, 500)
    private final String error;     // HTTP 상태 메시지 (e.g., Bad Request, Not Found, Internal Server Error)
    private final String code;      // 비즈니스 로직에 따른 커스텀 에러 코드 (e.g., USER_NOT_FOUND, INVALID_PASSWORD)
    private final String message;   // 사용자에게 보여줄 상세 메시지
    private final String path;      // 요청된 API 경로

    // 유효성 검사 오류 목록 (선택 사항)
    private final List<ValidationError> errors;

    @Getter
    @RequiredArgsConstructor
    public static class ValidationError {
        private final String field;
        private final String defaultMessage;
    }

    public static ErrorResponse of(int status, String error, String code, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse of(int status, String error, String code, String message, String path, List<ValidationError> errors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .errors(errors)
                .build();
    }
}