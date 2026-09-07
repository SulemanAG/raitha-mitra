package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating location coordinates and administrative region metadata.
 * Includes validation for coordinate range limits.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record UpdateLocationRequestDto(
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
        BigDecimal latitude,

        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
        BigDecimal longitude,

        @Size(max = 100, message = "State cannot exceed 100 characters")
        String state,

        @Size(max = 100, message = "District cannot exceed 100 characters")
        String district,

        @Size(max = 100, message = "Taluk cannot exceed 100 characters")
        String taluk,

        @Size(max = 100, message = "Village cannot exceed 100 characters")
        String village,

        String locationSource,

        Long version
) {
}
