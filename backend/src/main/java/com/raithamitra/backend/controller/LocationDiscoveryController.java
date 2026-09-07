package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.request.UpdateLocationRequestDto;
import com.raithamitra.backend.dto.response.MachineryDiscoveryResponseDto;
import com.raithamitra.backend.dto.response.MachineryResponseDto;
import com.raithamitra.backend.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST Controller providing spatial discovery search endpoints and machinery location updates.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1")
public class LocationDiscoveryController {

    private final LocationService locationService;

    public LocationDiscoveryController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/discovery/machinery/nearby")
    public ResponseEntity<Page<MachineryDiscoveryResponseDto>> discoverNearbyMachinery(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MachineryDiscoveryResponseDto> response = locationService.discoverNearbyMachinery(
                latitude,
                longitude,
                radiusKm,
                category,
                startDate,
                endDate,
                pageable
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/machinery/{id}/location")
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<MachineryResponseDto> updateMachineryLocation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLocationRequestDto requestDto
    ) {
        MachineryResponseDto response = locationService.updateMachineryLocation(id, requestDto);
        return ResponseEntity.ok(response);
    }
}
