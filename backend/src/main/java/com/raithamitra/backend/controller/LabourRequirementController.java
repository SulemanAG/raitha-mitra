package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.request.CreateLabourRequirementRequestDto;
import com.raithamitra.backend.dto.request.UpdateLabourRequirementRequestDto;
import com.raithamitra.backend.dto.request.UpdateRequirementStatusRequestDto;
import com.raithamitra.backend.dto.response.LabourRequirementResponseDto;
import com.raithamitra.backend.service.LabourRequirementService;
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

import java.util.UUID;

/**
 * REST Controller providing CRUD operations and paginated workforce discovery search endpoints for Labour Requirements.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/labour-requirements")
public class LabourRequirementController {

    private final LabourRequirementService labourRequirementService;

    public LabourRequirementController(LabourRequirementService labourRequirementService) {
        this.labourRequirementService = labourRequirementService;
    }

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<LabourRequirementResponseDto> createRequirement(@Valid @RequestBody CreateLabourRequirementRequestDto requestDto) {
        LabourRequirementResponseDto response = labourRequirementService.createRequirement(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<Page<LabourRequirementResponseDto>> getMyRequirements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(labourRequirementService.getMyRequirements(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LabourRequirementResponseDto>> searchRequirements(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(labourRequirementService.searchRequirements(location, taskType, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabourRequirementResponseDto> getRequirementById(@PathVariable UUID id) {
        return ResponseEntity.ok(labourRequirementService.getRequirementById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<LabourRequirementResponseDto> updateRequirement(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLabourRequirementRequestDto requestDto
    ) {
        return ResponseEntity.ok(labourRequirementService.updateRequirement(id, requestDto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<LabourRequirementResponseDto> updateRequirementStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRequirementStatusRequestDto requestDto
    ) {
        return ResponseEntity.ok(labourRequirementService.updateRequirementStatus(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<Void> deleteRequirement(@PathVariable UUID id) {
        labourRequirementService.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }
}
