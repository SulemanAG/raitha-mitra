package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateMachineryOwnerProfileRequestDto;
import com.raithamitra.backend.dto.response.MachineryOwnerProfileResponseDto;

import java.util.UUID;

/**
 * Service interface managing Machinery Owner Profile business operations and role auto-assignment.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface MachineryOwnerProfileService {

    MachineryOwnerProfileResponseDto createOrUpdateProfile(CreateMachineryOwnerProfileRequestDto requestDto);

    MachineryOwnerProfileResponseDto getMyProfile();

    MachineryOwnerProfileResponseDto getProfileByUserId(UUID userId);
}
