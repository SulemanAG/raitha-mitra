package com.raithamitra.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Standardized API Error Response DTO for consistent backend exception representation.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String message,
        String path,
        List<FieldErrorDetail> fieldErrors
) {
    public record FieldErrorDetail(
            String field,
            String rejectedValue,
            String message
    ) {
    }
}
