package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for OTP Generation requests.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record RequestOtpRequestDto(
        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid mobile number format")
        String mobileNumber
) {
}
