package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateMachineryRequestDto;
import com.raithamitra.backend.dto.request.UpdateMachineryRequestDto;
import com.raithamitra.backend.dto.response.MachineryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface managing Machinery Asset registration, ownership enforcement, operational status,
 * and paginated machinery discovery.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface MachineryService {

    MachineryResponseDto registerMachinery(CreateMachineryRequestDto requestDto);

    MachineryResponseDto getMachineryById(UUID id);

    Page<MachineryResponseDto> getMyMachinery(Pageable pageable);

    MachineryResponseDto updateMachinery(UUID id, UpdateMachineryRequestDto requestDto);

    MachineryResponseDto toggleMachineryStatus(UUID id, String status);

    void deleteMachinery(UUID id);

    Page<MachineryResponseDto> searchMachinery(String location, String category, String status, Pageable pageable);
}
