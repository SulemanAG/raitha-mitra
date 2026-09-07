package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.CreateLabourerProfileRequestDto;
import com.raithamitra.backend.dto.request.UpdateLabourerProfileRequestDto;
import com.raithamitra.backend.dto.response.LabourerProfileResponseDto;
import com.raithamitra.backend.entity.AvailabilityStatus;
import com.raithamitra.backend.entity.LabourSkill;
import com.raithamitra.backend.entity.LabourerProfileEntity;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.exception.ResourceAlreadyExistsException;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.LabourerProfileRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.service.LabourerProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation executing Labourer Profile business rules and paginated workforce discovery.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class LabourerProfileServiceImpl implements LabourerProfileService {

    private static final Logger log = LoggerFactory.getLogger(LabourerProfileServiceImpl.class);

    private final LabourerProfileRepository labourerProfileRepository;
    private final UserRepository userRepository;

    public LabourerProfileServiceImpl(LabourerProfileRepository labourerProfileRepository, UserRepository userRepository) {
        this.labourerProfileRepository = labourerProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public LabourerProfileResponseDto createLabourerProfile(CreateLabourerProfileRequestDto requestDto) {
        log.info("Attempting to create Labourer profile for user ID: {}", requestDto.userId());

        UserEntity userEntity = userRepository.findById(requestDto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.userId()));

        if (labourerProfileRepository.existsByUserId(requestDto.userId())) {
            throw new ResourceAlreadyExistsException("LabourerProfile", "userId", requestDto.userId());
        }

        // Add LABOURER role to user if not present
        userEntity.getRoles().add(UserRole.LABOURER);

        AvailabilityStatus status = parseAvailabilityStatus(requestDto.availabilityStatus());
        Set<LabourSkill> parsedSkills = parseSkills(requestDto.skills());

        LabourerProfileEntity profileEntity = new LabourerProfileEntity(
                userEntity,
                requestDto.fullName(),
                requestDto.dailyWageRate(),
                requestDto.experienceYears(),
                status,
                parsedSkills
        );

        userEntity.setLabourerProfile(profileEntity);
        userRepository.save(userEntity);

        LabourerProfileEntity savedProfile = labourerProfileRepository.findByUserId(requestDto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("LabourerProfile", "userId", requestDto.userId()));

        log.info("Successfully created Labourer profile ID: {} for user ID: {}", savedProfile.getId(), requestDto.userId());
        return mapToLabourerProfileResponseDto(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public LabourerProfileResponseDto getLabourerProfileByUserId(UUID userId) {
        LabourerProfileEntity profile = labourerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LabourerProfile", "userId", userId));
        return mapToLabourerProfileResponseDto(profile);
    }

    @Override
    @Transactional
    public LabourerProfileResponseDto updateLabourerProfile(UUID userId, UpdateLabourerProfileRequestDto requestDto) {
        log.info("Updating Labourer profile for user ID: {}", userId);

        LabourerProfileEntity profile = labourerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LabourerProfile", "userId", userId));

        profile.setFullName(requestDto.fullName());
        profile.setDailyWageRate(requestDto.dailyWageRate());
        profile.setExperienceYears(requestDto.experienceYears());
        
        if (requestDto.availabilityStatus() != null) {
            profile.setAvailabilityStatus(parseAvailabilityStatus(requestDto.availabilityStatus()));
        }
        if (requestDto.skills() != null) {
            profile.setSkills(parseSkills(requestDto.skills()));
        }

        LabourerProfileEntity updatedProfile = labourerProfileRepository.save(profile);
        return mapToLabourerProfileResponseDto(updatedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabourerProfileResponseDto> searchLabourers(String availabilityStatus, String skill, Pageable pageable) {
        AvailabilityStatus status = availabilityStatus != null ? parseAvailabilityStatus(availabilityStatus) : AvailabilityStatus.AVAILABLE;

        if (skill != null && !skill.isBlank()) {
            LabourSkill parsedSkill = LabourSkill.valueOf(skill.toUpperCase().trim());
            return labourerProfileRepository.findByAvailabilityAndSkill(status, parsedSkill, pageable)
                    .map(this::mapToLabourerProfileResponseDto);
        }

        return labourerProfileRepository.findByAvailabilityStatus(status, pageable)
                .map(this::mapToLabourerProfileResponseDto);
    }

    private LabourerProfileResponseDto mapToLabourerProfileResponseDto(LabourerProfileEntity entity) {
        Set<String> skillNames = entity.getSkills()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new LabourerProfileResponseDto(
                entity.getId(),
                entity.getUser().getId(),
                entity.getFullName(),
                entity.getDailyWageRate(),
                entity.getExperienceYears(),
                entity.getAvailabilityStatus().name(),
                skillNames,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private AvailabilityStatus parseAvailabilityStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            return AvailabilityStatus.AVAILABLE;
        }
        try {
            return AvailabilityStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return AvailabilityStatus.AVAILABLE;
        }
    }

    private Set<LabourSkill> parseSkills(Set<String> skillStrs) {
        if (skillStrs == null) return new HashSet<>();
        Set<LabourSkill> skills = new HashSet<>();
        for (String str : skillStrs) {
            if (str != null && !str.isBlank()) {
                try {
                    skills.add(LabourSkill.valueOf(str.toUpperCase().trim()));
                } catch (IllegalArgumentException ignored) {
                    // Ignore unknown skill strings gracefully
                }
            }
        }
        return skills;
    }
}
