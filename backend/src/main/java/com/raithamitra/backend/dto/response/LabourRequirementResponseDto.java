package com.raithamitra.backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing detailed Labour Requirement details in API responses.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record LabourRequirementResponseDto(
        UUID id,
        UUID farmerUserId,
        String farmerName,
        String farmerMobile,
        String title,
        String description,
        String taskType,
        String location,
        int requiredWorkersCount,
        BigDecimal dailyOfferedWage,
        LocalDate workDate,
        String status,
        Set<String> requiredSkills,
        Instant createdAt,
        Instant updatedAt
) {
}
