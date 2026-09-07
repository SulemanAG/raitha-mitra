package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.request.CreateMachineryRequestDto;
import com.raithamitra.backend.dto.request.UpdateMachineryRequestDto;
import com.raithamitra.backend.dto.response.MachineryResponseDto;
import com.raithamitra.backend.service.MachineryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * REST Controller providing CRUD endpoints and public discovery search for agricultural machinery assets.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/machinery")
public class MachineryController {

    private final MachineryService machineryService;

    public MachineryController(MachineryService machineryService) {
        this.machineryService = machineryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<MachineryResponseDto> registerMachinery(@Valid @RequestBody CreateMachineryRequestDto requestDto) {
        MachineryResponseDto response = machineryService.registerMachinery(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<Page<MachineryResponseDto>> getMyMachinery(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(machineryService.getMyMachinery(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MachineryResponseDto>> searchMachinery(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(machineryService.searchMachinery(location, category, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineryResponseDto> getMachineryById(@PathVariable UUID id) {
        return ResponseEntity.ok(machineryService.getMachineryById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<MachineryResponseDto> updateMachinery(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMachineryRequestDto requestDto
    ) {
        return ResponseEntity.ok(machineryService.updateMachinery(id, requestDto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<MachineryResponseDto> toggleMachineryStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> payload
    ) {
        String statusStr = payload.getOrDefault("status", "ACTIVE");
        return ResponseEntity.ok(machineryService.toggleMachineryStatus(id, statusStr));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MACHINERY_OWNER')")
    public ResponseEntity<Void> deleteMachinery(@PathVariable UUID id) {
        machineryService.deleteMachinery(id);
        return ResponseEntity.noContent().build();
    }
}
