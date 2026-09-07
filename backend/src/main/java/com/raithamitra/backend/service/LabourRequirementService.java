package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateLabourRequirementRequestDto;
import com.raithamitra.backend.dto.request.UpdateLabourRequirementRequestDto;
import com.raithamitra.backend.dto.request.UpdateRequirementStatusRequestDto;
import com.raithamitra.backend.dto.response.LabourRequirementResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface managing Labour Requirement posting lifecycle, farmer ownership validation,
 * and paginated workforce discovery.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface LabourRequirementService {

    LabourRequirementResponseDto createRequirement(CreateLabourRequirementRequestDto requestDto);

    LabourRequirementResponseDto getRequirementById(UUID id);

    Page<LabourRequirementResponseDto> getMyRequirements(Pageable pageable);

    LabourRequirementResponseDto updateRequirement(UUID id, UpdateLabourRequirementRequestDto requestDto);

    LabourRequirementResponseDto updateRequirementStatus(UUID id, UpdateRequirementStatusRequestDto requestDto);

    void deleteRequirement(UUID id);

    Page<LabourRequirementResponseDto> searchRequirements(String location, String taskType, String status, Pageable pageable);
}
