package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating an existing Farmer profile.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record UpdateFarmerProfileRequestDto(

        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name cannot exceed 100 characters")
        String fullName,

        @Size(max = 150, message = "Farm location cannot exceed 150 characters")
        String farmLocation,

        @DecimalMin(value = "0.0", inclusive = true, message = "Farm size acres cannot be negative")
        BigDecimal farmSizeAcres,

        @Size(max = 255, message = "Preferred crop types cannot exceed 255 characters")
        String preferredCropTypes
) {
}
