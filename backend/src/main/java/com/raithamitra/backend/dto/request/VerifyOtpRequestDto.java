package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for OTP Verification requests.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record VerifyOtpRequestDto(
        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid mobile number format")
        String mobileNumber,

        @NotBlank(message = "OTP is required")
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
        String otp
) {
}
