package com.nhnacademy.illuwa.common.exception;

import lombok.Getter;

// 예외 발생 클래스 -> Exception Handler
@Getter
public class CommonException extends RuntimeException {
    private final int status;
    private final String message;

    public CommonException(int status, String message) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
