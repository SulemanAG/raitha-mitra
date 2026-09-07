package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.request.CreateLabourerProfileRequestDto;
import com.raithamitra.backend.dto.request.UpdateLabourerProfileRequestDto;
import com.raithamitra.backend.dto.response.LabourerProfileResponseDto;
import com.raithamitra.backend.service.LabourerProfileService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller exposing Labourer profile management & paginated workforce discovery API endpoints.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/labourers")
public class LabourerProfileController {

    private final LabourerProfileService labourerProfileService;

    public LabourerProfileController(LabourerProfileService labourerProfileService) {
        this.labourerProfileService = labourerProfileService;
    }

    @PostMapping
    public ResponseEntity<LabourerProfileResponseDto> createLabourerProfile(@Valid @RequestBody CreateLabourerProfileRequestDto requestDto) {
        LabourerProfileResponseDto createdProfile = labourerProfileService.createLabourerProfile(requestDto);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<LabourerProfileResponseDto> getLabourerProfileByUserId(@PathVariable UUID userId) {
        LabourerProfileResponseDto profile = labourerProfileService.getLabourerProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<LabourerProfileResponseDto> updateLabourerProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateLabourerProfileRequestDto requestDto
    ) {
        LabourerProfileResponseDto updatedProfile = labourerProfileService.updateLabourerProfile(userId, requestDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping
    public ResponseEntity<Page<LabourerProfileResponseDto>> searchLabourers(
            @RequestParam(required = false, defaultValue = "AVAILABLE") String availabilityStatus,
            @RequestParam(required = false) String skill,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<LabourerProfileResponseDto> labourers = labourerProfileService.searchLabourers(availabilityStatus, skill, pageable);
        return ResponseEntity.ok(labourers);
    }
}
