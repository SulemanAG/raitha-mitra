package com.raithamitra.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a rental request creation or acceptance conflicts with an existing accepted booking period.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public class RentalPeriodConflictException extends BaseException {

    public RentalPeriodConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "RENTAL_PERIOD_CONFLICT");
    }
}
