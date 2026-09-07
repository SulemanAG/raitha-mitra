package com.raithamitra.backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing Farmer profile response contract.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record FarmerProfileResponseDto(
        UUID id,
        UUID userId,
        String fullName,
        String farmLocation,
        BigDecimal farmSizeAcres,
        String preferredCropTypes,
        Instant createdAt,
        Instant updatedAt
) {
}
