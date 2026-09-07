package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for creating a new machinery rental request.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record CreateRentalRequestDto(
        @NotNull(message = "Machinery ID is required")
        UUID machineryId,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date must be today or in the future")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        @FutureOrPresent(message = "End date must be today or in the future")
        LocalDate endDate,

        String rentalUnit,

        @Min(value = 1, message = "Estimated units must be at least 1")
        int estimatedUnits,

        String renterNotes
) {
}
