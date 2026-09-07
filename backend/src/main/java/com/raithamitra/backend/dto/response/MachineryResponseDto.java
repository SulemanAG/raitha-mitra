package com.raithamitra.backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing agricultural machine details in API responses.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record MachineryResponseDto(
        UUID id,
        UUID ownerUserId,
        String ownerName,
        String ownerMobile,
        String name,
        String modelNumber,
        String category,
        Integer hpRating,
        String location,
        BigDecimal dailyRate,
        BigDecimal hourlyRate,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
