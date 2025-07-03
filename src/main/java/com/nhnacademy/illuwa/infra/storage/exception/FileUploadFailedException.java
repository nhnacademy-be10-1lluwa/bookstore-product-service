package com.nhnacademy.illuwa.infra.storage.exception;

public class FileUploadFailedException extends RuntimeException {
    public FileUploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}