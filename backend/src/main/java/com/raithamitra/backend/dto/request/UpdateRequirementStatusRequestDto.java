package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for updating requirement posting status.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record UpdateRequirementStatusRequestDto(
        @NotBlank(message = "Status is required")
        String status
) {
}
