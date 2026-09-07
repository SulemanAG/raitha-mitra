package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.request.CreateRentalRequestDto;
import com.raithamitra.backend.dto.request.UpdateRentalStatusRequestDto;
import com.raithamitra.backend.dto.response.RentalRequestResponseDto;
import com.raithamitra.backend.service.RentalRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller providing rental booking request submission, owner approvals, rejections, and cancellations.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/machinery-rentals")
public class RentalRequestController {

    private final RentalRequestService rentalRequestService;

    public RentalRequestController(RentalRequestService rentalRequestService) {
        this.rentalRequestService = rentalRequestService;
    }

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<RentalRequestResponseDto> createRentalRequest(@Valid @RequestBody CreateRentalRequestDto requestDto) {
        RentalRequestResponseDto response = rentalRequestService.createRentalRequest(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Page<RentalRequestResponseDto>> getMyRentalRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(rentalRequestService.getMyRentalRequests(pageable));
    }

    @GetMapping("/owner-requests")
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<Page<RentalRequestResponseDto>> getOwnerRentalRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(rentalRequestService.getOwnerRentalRequests(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalRequestResponseDto> getRentalRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(rentalRequestService.getRentalRequestById(id));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<RentalRequestResponseDto> acceptRentalRequest(
            @PathVariable UUID id,
            @RequestBody(required = false) UpdateRentalStatusRequestDto requestDto
    ) {
        return ResponseEntity.ok(rentalRequestService.acceptRentalRequest(id, requestDto));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<RentalRequestResponseDto> rejectRentalRequest(
            @PathVariable UUID id,
            @RequestBody(required = false) UpdateRentalStatusRequestDto requestDto
    ) {
        return ResponseEntity.ok(rentalRequestService.rejectRentalRequest(id, requestDto));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<RentalRequestResponseDto> cancelRentalRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(rentalRequestService.cancelRentalRequest(id));
    }
}
