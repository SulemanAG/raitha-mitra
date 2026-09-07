package com.raithamitra.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a resource that already exists (e.g. duplicate mobile number).
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public class ResourceAlreadyExistsException extends BaseException {

    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.CONFLICT,
                "RESOURCE_ALREADY_EXISTS"
        );
    }
}
