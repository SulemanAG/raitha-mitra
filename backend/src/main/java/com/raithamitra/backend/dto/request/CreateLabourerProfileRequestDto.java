package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object for creating a new Labourer profile.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record CreateLabourerProfileRequestDto(

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name cannot exceed 100 characters")
        String fullName,

        @DecimalMin(value = "0.0", inclusive = true, message = "Daily wage rate cannot be negative")
        BigDecimal dailyWageRate,

        @Min(value = 0, message = "Experience years cannot be negative")
        Integer experienceYears,

        @Pattern(regexp = "^(AVAILABLE|BUSY_ON_JOB|UNAVAILABLE)$", message = "Availability status must be AVAILABLE, BUSY_ON_JOB, or UNAVAILABLE")
        String availabilityStatus,

        Set<String> skills
) {
}
