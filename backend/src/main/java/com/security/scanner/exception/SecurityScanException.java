package com.security.scanner.exception;

public class SecurityScanException extends RuntimeException {
    public SecurityScanException(String message) {
        super(message);
    }

    public SecurityScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
