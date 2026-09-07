package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.request.CreateFarmerProfileRequestDto;
import com.raithamitra.backend.dto.request.UpdateFarmerProfileRequestDto;
import com.raithamitra.backend.dto.response.FarmerProfileResponseDto;
import com.raithamitra.backend.service.FarmerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller exposing Farmer profile management API endpoints following REST conventions.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/farmers")
public class FarmerProfileController {

    private final FarmerProfileService farmerProfileService;

    public FarmerProfileController(FarmerProfileService farmerProfileService) {
        this.farmerProfileService = farmerProfileService;
    }

    @PostMapping
    public ResponseEntity<FarmerProfileResponseDto> createFarmerProfile(@Valid @RequestBody CreateFarmerProfileRequestDto requestDto) {
        FarmerProfileResponseDto createdProfile = farmerProfileService.createFarmerProfile(requestDto);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<FarmerProfileResponseDto> getFarmerProfileByUserId(@PathVariable UUID userId) {
        FarmerProfileResponseDto profile = farmerProfileService.getFarmerProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<FarmerProfileResponseDto> updateFarmerProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateFarmerProfileRequestDto requestDto
    ) {
        FarmerProfileResponseDto updatedProfile = farmerProfileService.updateFarmerProfile(userId, requestDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
