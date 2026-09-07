package com.raithamitra.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Root Application Runtime Exception providing HTTP status code and machine-readable error code.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public abstract class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    protected BaseException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
