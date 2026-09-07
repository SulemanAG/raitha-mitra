package com.raithamitra.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when geographic coordinates are invalid, out-of-bounds, partial, or malformed.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public class InvalidCoordinatesException extends BaseException {

    public InvalidCoordinatesException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_COORDINATES");
    }
}
