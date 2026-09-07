package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Data Transfer Object for creating a new Labour Requirement posting.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record CreateLabourRequirementRequestDto(
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title cannot exceed 150 characters")
        String title,

        String description,

        @NotBlank(message = "Task type is required")
        String taskType,

        @NotBlank(message = "Location is required")
        @Size(max = 150, message = "Location cannot exceed 150 characters")
        String location,

        @Min(value = 1, message = "Required workers count must be at least 1")
        int requiredWorkersCount,

        @NotNull(message = "Daily offered wage is required")
        @DecimalMin(value = "1.00", message = "Daily offered wage must be positive")
        BigDecimal dailyOfferedWage,

        @NotNull(message = "Work date is required")
        @FutureOrPresent(message = "Work date must be today or in the future")
        LocalDate workDate,

        Set<String> requiredSkills
) {
}
