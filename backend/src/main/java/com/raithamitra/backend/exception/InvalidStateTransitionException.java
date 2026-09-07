package com.raithamitra.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an illegal state transition is attempted on a domain entity lifecycle.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public class InvalidStateTransitionException extends BaseException {

    public InvalidStateTransitionException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_STATE_TRANSITION");
    }
}
