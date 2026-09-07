package com.raithamitra.backend.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing user detail API response contract.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record UserResponseDto(
        UUID id,
        String mobileNumber,
        String primaryRole,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
