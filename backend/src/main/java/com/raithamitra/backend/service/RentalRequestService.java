package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateRentalRequestDto;
import com.raithamitra.backend.dto.request.UpdateRentalStatusRequestDto;
import com.raithamitra.backend.dto.response.RentalRequestResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface managing Machinery Rental Requests, authoritative pricing,
 * concurrency-safe approval locking, and lifecycle state transitions.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface RentalRequestService {

    RentalRequestResponseDto createRentalRequest(CreateRentalRequestDto requestDto);

    RentalRequestResponseDto getRentalRequestById(UUID id);

    Page<RentalRequestResponseDto> getMyRentalRequests(Pageable pageable);

    Page<RentalRequestResponseDto> getOwnerRentalRequests(Pageable pageable);

    RentalRequestResponseDto acceptRentalRequest(UUID id, UpdateRentalStatusRequestDto requestDto);

    RentalRequestResponseDto rejectRentalRequest(UUID id, UpdateRentalStatusRequestDto requestDto);

    RentalRequestResponseDto cancelRentalRequest(UUID id);
}
