package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data Transfer Object for registering a new agricultural machine asset.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record CreateMachineryRequestDto(
        @NotBlank(message = "Machinery name is required")
        @Size(max = 150, message = "Name cannot exceed 150 characters")
        String name,

        @Size(max = 100, message = "Model number cannot exceed 100 characters")
        String modelNumber,

        @NotBlank(message = "Category is required")
        String category,

        @Min(value = 1, message = "HP rating must be positive")
        Integer hpRating,

        @NotBlank(message = "Location is required")
        @Size(max = 150, message = "Location cannot exceed 150 characters")
        String location,

        @NotNull(message = "Daily rate is required")
        @DecimalMin(value = "1.00", message = "Daily rate must be positive")
        BigDecimal dailyRate,

        @DecimalMin(value = "1.00", message = "Hourly rate must be positive")
        BigDecimal hourlyRate
) {
}
