package com.nhnacademy.illuwa.common.exception;

import lombok.Getter;

// TODO: (임시) 예외 발생 클래스 -> Exception Handler, 방식 확정되면 변경 필요
@Getter
public class ApiException extends RuntimeException {
    private final int status;
    private final String message;

    public ApiException(int status, String message) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
