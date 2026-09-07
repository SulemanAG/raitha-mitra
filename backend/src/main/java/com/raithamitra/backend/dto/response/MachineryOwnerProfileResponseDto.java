package com.raithamitra.backend.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing Machinery Owner Profile details in API responses.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record MachineryOwnerProfileResponseDto(
        UUID id,
        UUID userId,
        String fullName,
        String address,
        String contactNumber,
        Instant createdAt,
        Instant updatedAt
) {
}
