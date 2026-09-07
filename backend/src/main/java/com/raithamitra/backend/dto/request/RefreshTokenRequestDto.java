package com.raithamitra.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for refreshing JWT authentication tokens.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record RefreshTokenRequestDto(
        @NotBlank(message = "Token is required")
        String token
) {
}
