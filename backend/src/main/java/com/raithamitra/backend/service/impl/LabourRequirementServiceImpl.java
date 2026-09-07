package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.CreateLabourRequirementRequestDto;
import com.raithamitra.backend.dto.request.UpdateLabourRequirementRequestDto;
import com.raithamitra.backend.dto.request.UpdateRequirementStatusRequestDto;
import com.raithamitra.backend.dto.response.LabourRequirementResponseDto;
import com.raithamitra.backend.entity.FarmerProfileEntity;
import com.raithamitra.backend.entity.LabourRequirementEntity;
import com.raithamitra.backend.entity.RequirementStatus;
import com.raithamitra.backend.entity.TaskType;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.exception.UnauthorizedException;
import com.raithamitra.backend.repository.LabourRequirementRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.SecurityUtils;
import com.raithamitra.backend.service.LabourRequirementService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation handling Labour Requirement creation, retrieval, updates, status transitions,
 * server-side ownership enforcement, and discovery searching.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class LabourRequirementServiceImpl implements LabourRequirementService {

    private static final Logger log = LoggerFactory.getLogger(LabourRequirementServiceImpl.class);

    private final LabourRequirementRepository labourRequirementRepository;
    private final UserRepository userRepository;

    public LabourRequirementServiceImpl(LabourRequirementRepository labourRequirementRepository, UserRepository userRepository) {
        this.labourRequirementRepository = labourRequirementRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public LabourRequirementResponseDto createRequirement(CreateLabourRequirementRequestDto requestDto) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UserEntity farmerUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        TaskType taskTypeEnum = parseTaskType(requestDto.taskType());

        LabourRequirementEntity entity = new LabourRequirementEntity();
        entity.setFarmerUser(farmerUser);
        entity.setTitle(requestDto.title());
        entity.setDescription(requestDto.description());
        entity.setTaskType(taskTypeEnum);
        entity.setLocation(requestDto.location());
        entity.setRequiredWorkersCount(requestDto.requiredWorkersCount());
        entity.setDailyOfferedWage(requestDto.dailyOfferedWage());
        entity.setWorkDate(requestDto.workDate());
        entity.setStatus(RequirementStatus.OPEN);
        entity.setRequiredSkills(requestDto.requiredSkills());

        LabourRequirementEntity saved = labourRequirementRepository.save(entity);
        log.info("Created labour requirement posting ID: {} by farmer ID: {}", saved.getId(), currentUserId);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LabourRequirementResponseDto getRequirementById(UUID id) {
        LabourRequirementEntity entity = labourRequirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabourRequirement", "id", id));
        return mapToDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabourRequirementResponseDto> getMyRequirements(Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return labourRequirementRepository.findByFarmerUserId(currentUserId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public LabourRequirementResponseDto updateRequirement(UUID id, UpdateLabourRequirementRequestDto requestDto) {
        LabourRequirementEntity entity = labourRequirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabourRequirement", "id", id));

        verifyOwnership(entity);

        entity.setTitle(requestDto.title());
        entity.setDescription(requestDto.description());
        entity.setTaskType(parseTaskType(requestDto.taskType()));
        entity.setLocation(requestDto.location());
        entity.setRequiredWorkersCount(requestDto.requiredWorkersCount());
        entity.setDailyOfferedWage(requestDto.dailyOfferedWage());
        entity.setWorkDate(requestDto.workDate());
        if (requestDto.requiredSkills() != null) {
            entity.setRequiredSkills(requestDto.requiredSkills());
        }

        LabourRequirementEntity updated = labourRequirementRepository.save(entity);
        log.info("Updated labour requirement posting ID: {}", updated.getId());
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public LabourRequirementResponseDto updateRequirementStatus(UUID id, UpdateRequirementStatusRequestDto requestDto) {
        LabourRequirementEntity entity = labourRequirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabourRequirement", "id", id));

        verifyOwnership(entity);

        RequirementStatus newStatus = parseStatus(requestDto.status());
        entity.setStatus(newStatus);

        LabourRequirementEntity updated = labourRequirementRepository.save(entity);
        log.info("Updated status for labour requirement ID: {} to {}", updated.getId(), newStatus);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteRequirement(UUID id) {
        LabourRequirementEntity entity = labourRequirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabourRequirement", "id", id));

        verifyOwnership(entity);

        labourRequirementRepository.delete(entity);
        log.info("Deleted labour requirement ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabourRequirementResponseDto> searchRequirements(String location, String taskType, String status, Pageable pageable) {
        Specification<LabourRequirementEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(location)) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase().trim() + "%"));
            }

            if (StringUtils.hasText(taskType)) {
                try {
                    TaskType tt = TaskType.valueOf(taskType.toUpperCase().trim());
                    predicates.add(cb.equal(root.get("taskType"), tt));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (StringUtils.hasText(status)) {
                try {
                    RequirementStatus rs = RequirementStatus.valueOf(status.toUpperCase().trim());
                    predicates.add(cb.equal(root.get("status"), rs));
                } catch (IllegalArgumentException ignored) {
                }
            } else {
                predicates.add(cb.equal(root.get("status"), RequirementStatus.OPEN));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return labourRequirementRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    private void verifyOwnership(LabourRequirementEntity entity) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        if (!entity.getFarmerUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User is not authorized to modify this requirement posting");
        }
    }

    private LabourRequirementResponseDto mapToDto(LabourRequirementEntity entity) {
        UserEntity farmer = entity.getFarmerUser();
        String farmerName = "Farmer";
        if (farmer.getFarmerProfile() != null && StringUtils.hasText(farmer.getFarmerProfile().getFullName())) {
            farmerName = farmer.getFarmerProfile().getFullName();
        }

        return new LabourRequirementResponseDto(
                entity.getId(),
                farmer.getId(),
                farmerName,
                farmer.getMobileNumber(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getTaskType().name(),
                entity.getLocation(),
                entity.getRequiredWorkersCount(),
                entity.getDailyOfferedWage(),
                entity.getWorkDate(),
                entity.getStatus().name(),
                entity.getRequiredSkills(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private TaskType parseTaskType(String taskTypeStr) {
        try {
            return TaskType.valueOf(taskTypeStr.toUpperCase().trim());
        } catch (Exception e) {
            return TaskType.GENERAL_LABOUR;
        }
    }

    private RequirementStatus parseStatus(String statusStr) {
        try {
            return RequirementStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (Exception e) {
            return RequirementStatus.OPEN;
        }
    }
}
