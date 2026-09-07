package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.request.CreateMachineryOwnerProfileRequestDto;
import com.raithamitra.backend.dto.response.MachineryOwnerProfileResponseDto;
import com.raithamitra.backend.service.MachineryOwnerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller providing profile management endpoints for Machinery Owners.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/machinery-owner-profile")
public class MachineryOwnerProfileController {

    private final MachineryOwnerProfileService profileService;

    public MachineryOwnerProfileController(MachineryOwnerProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<MachineryOwnerProfileResponseDto> createOrUpdateProfile(
            @Valid @RequestBody CreateMachineryOwnerProfileRequestDto requestDto
    ) {
        return ResponseEntity.ok(profileService.createOrUpdateProfile(requestDto));
    }

    @GetMapping("/me")
    public ResponseEntity<MachineryOwnerProfileResponseDto> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<MachineryOwnerProfileResponseDto> getProfileByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }
}
