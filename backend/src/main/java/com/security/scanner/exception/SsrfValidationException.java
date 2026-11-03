package com.security.scanner.exception;

public class SsrfValidationException extends RuntimeException {
    public SsrfValidationException(String message) {
        super(message);
    }
}
