package com.raithamitra.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested domain resource (e.g. User) does not exist in persistence.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND"
        );
    }
}
