package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateFarmerProfileRequestDto;
import com.raithamitra.backend.dto.request.UpdateFarmerProfileRequestDto;
import com.raithamitra.backend.dto.response.FarmerProfileResponseDto;

import java.util.UUID;

/**
 * Service Interface defining Farmer Profile domain operations and business constraints.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface FarmerProfileService {

    FarmerProfileResponseDto createFarmerProfile(CreateFarmerProfileRequestDto requestDto);

    FarmerProfileResponseDto getFarmerProfileByUserId(UUID userId);

    FarmerProfileResponseDto updateFarmerProfile(UUID userId, UpdateFarmerProfileRequestDto requestDto);
}
