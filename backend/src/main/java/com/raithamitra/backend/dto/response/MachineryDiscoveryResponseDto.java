package com.raithamitra.backend.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object representing privacy-safe Machinery Discovery search results.
 * Exposes backend-calculated approximate distance, coarse administrative regions, and availability.
 * STRICT PRIVACY GUARANTEE: Does NOT expose exact private latitude and longitude coordinates to discovery callers.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record MachineryDiscoveryResponseDto(
        UUID id,
        UUID ownerUserId,
        String ownerName,
        String name,
        String modelNumber,
        String category,
        Integer hpRating,
        String locationName,
        String district,
        String village,
        Double approximateDistanceKm,
        BigDecimal dailyRate,
        BigDecimal hourlyRate,
        String status,
        boolean availableForDates
) {
}
