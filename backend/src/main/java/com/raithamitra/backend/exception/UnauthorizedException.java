package com.raithamitra.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication credentials or tokens are missing or invalid.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
