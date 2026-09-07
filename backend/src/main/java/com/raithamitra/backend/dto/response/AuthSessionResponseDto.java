package com.raithamitra.backend.dto.response;

/**
 * Data Transfer Object representing authentication session details after successful verification.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record AuthSessionResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponseDto user,
        boolean isNewUser
) {
    public AuthSessionResponseDto(String accessToken, long expiresIn, UserResponseDto user, boolean isNewUser) {
        this(accessToken, "Bearer", expiresIn, user, isNewUser);
    }
}
