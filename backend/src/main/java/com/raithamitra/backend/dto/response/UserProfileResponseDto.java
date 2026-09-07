package com.raithamitra.backend.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Composite Data Transfer Object summarizing User account identity, roles, account status,
 * and optional associated Farmer/Labourer domain profiles.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public record UserProfileResponseDto(
        UUID id,
        String mobileNumber,
        String primaryRole,
        String accountStatus,
        Set<String> roles,
        FarmerProfileResponseDto farmerProfile,
        LabourerProfileResponseDto labourerProfile,
        Instant createdAt,
        Instant updatedAt
) {
}
