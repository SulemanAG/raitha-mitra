package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating a Machinery Owner Profile.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record CreateMachineryOwnerProfileRequestDto(
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name cannot exceed 100 characters")
        String fullName,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid contact number format")
        String contactNumber
) {
}
