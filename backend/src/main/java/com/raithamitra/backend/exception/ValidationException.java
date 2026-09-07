package com.raithamitra.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when validation or business constraint checks fail.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_FAILED");
    }
}
