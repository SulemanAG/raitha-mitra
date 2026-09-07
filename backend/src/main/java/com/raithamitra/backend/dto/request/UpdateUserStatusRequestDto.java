package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for updating account status.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record UpdateUserStatusRequestDto(

        @NotBlank(message = "Account status is required")
        @Pattern(regexp = "^(ACTIVE|PENDING_VERIFICATION|SUSPENDED|DEACTIVATED)$", message = "Account status must be ACTIVE, PENDING_VERIFICATION, SUSPENDED, or DEACTIVATED")
        String accountStatus
) {
}
