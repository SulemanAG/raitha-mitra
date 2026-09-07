package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for creating a new User.
 * Includes Bean Validation constraints for canonical mobile numbers (+91XXXXXXXXXX) and primary roles.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record CreateUserRequestDto(

        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^\\+91[6-9]\\d{9}$", message = "Mobile number must be a valid E.164 Indian mobile number (+91XXXXXXXXXX)")
        String mobileNumber,

        @NotBlank(message = "Primary role is required")
        @Pattern(regexp = "^(FARMER|LABOURER|MACHINERY_OWNER|ADMIN)$", message = "Primary role must be FARMER, LABOURER, MACHINERY_OWNER, or ADMIN")
        String primaryRole
) {
}
