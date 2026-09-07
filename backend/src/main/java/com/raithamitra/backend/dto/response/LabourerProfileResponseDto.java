package com.raithamitra.backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing Labourer profile response contract.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record LabourerProfileResponseDto(
        UUID id,
        UUID userId,
        String fullName,
        BigDecimal dailyWageRate,
        Integer experienceYears,
        String availabilityStatus,
        Set<String> skills,
        Instant createdAt,
        Instant updatedAt
) {
}
