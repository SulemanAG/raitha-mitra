package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for updating machinery rental request status (e.g. ACCEPT, REJECT, CANCEL).
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record UpdateRentalStatusRequestDto(
        @NotBlank(message = "Status is required")
        String status,

        String ownerNotes
) {
}
