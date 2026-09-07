package com.raithamitra.backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object representing machinery rental request booking details in API responses.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record RentalRequestResponseDto(
        UUID id,
        UUID machineryId,
        String machineryName,
        String machineryCategory,
        UUID ownerUserId,
        String ownerName,
        String ownerMobile,
        UUID renterUserId,
        String renterMobile,
        LocalDate startDate,
        LocalDate endDate,
        String rentalUnit,
        int estimatedUnits,
        BigDecimal ratePerUnit,
        BigDecimal totalAmount,
        String status,
        String renterNotes,
        String ownerNotes,
        Instant createdAt,
        Instant updatedAt
) {
}
