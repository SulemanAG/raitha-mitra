package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateLabourerProfileRequestDto;
import com.raithamitra.backend.dto.request.UpdateLabourerProfileRequestDto;
import com.raithamitra.backend.dto.response.LabourerProfileResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service Interface defining Labourer Profile operations and paginated discovery queries.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface LabourerProfileService {

    LabourerProfileResponseDto createLabourerProfile(CreateLabourerProfileRequestDto requestDto);

    LabourerProfileResponseDto getLabourerProfileByUserId(UUID userId);

    LabourerProfileResponseDto updateLabourerProfile(UUID userId, UpdateLabourerProfileRequestDto requestDto);

    Page<LabourerProfileResponseDto> searchLabourers(String availabilityStatus, String skill, Pageable pageable);
}
