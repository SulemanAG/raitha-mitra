package com.raithamitra.backend.dto.response;

import java.time.Instant;

/**
 * Data Transfer Object for backend health and status check API response.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record HealthResponseDto(
        String status,
        String appName,
        String version,
        Instant timestamp
) {
}
