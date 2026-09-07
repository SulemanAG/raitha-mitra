package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.CreateMachineryOwnerProfileRequestDto;
import com.raithamitra.backend.dto.response.MachineryOwnerProfileResponseDto;
import com.raithamitra.backend.entity.MachineryOwnerProfileEntity;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.MachineryOwnerProfileRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.SecurityUtils;
import com.raithamitra.backend.service.MachineryOwnerProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation managing Machinery Owner Profile registration, updates, and role assignment.
 * Automatically grants UserRole.MACHINERY_OWNER to the user upon profile registration.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class MachineryOwnerProfileServiceImpl implements MachineryOwnerProfileService {

    private static final Logger log = LoggerFactory.getLogger(MachineryOwnerProfileServiceImpl.class);

    private final MachineryOwnerProfileRepository profileRepository;
    private final UserRepository userRepository;

    public MachineryOwnerProfileServiceImpl(MachineryOwnerProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MachineryOwnerProfileResponseDto createOrUpdateProfile(CreateMachineryOwnerProfileRequestDto requestDto) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UserEntity user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        // Auto-assign MACHINERY_OWNER role if absent
        if (!user.getRoles().contains(UserRole.MACHINERY_OWNER)) {
            user.getRoles().add(UserRole.MACHINERY_OWNER);
            userRepository.save(user);
            log.info("Granted MACHINERY_OWNER role to user ID: {}", currentUserId);
        }

        Optional<MachineryOwnerProfileEntity> existingOpt = profileRepository.findByUserId(currentUserId);
        MachineryOwnerProfileEntity profile;
        if (existingOpt.isPresent()) {
            profile = existingOpt.get();
        } else {
            profile = new MachineryOwnerProfileEntity();
            profile.setUser(user);
        }

        profile.setFullName(requestDto.fullName());
        profile.setAddress(requestDto.address());
        profile.setContactNumber(requestDto.contactNumber());

        MachineryOwnerProfileEntity saved = profileRepository.save(profile);
        log.info("Saved MachineryOwnerProfile ID: {} for user ID: {}", saved.getId(), currentUserId);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MachineryOwnerProfileResponseDto getMyProfile() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        MachineryOwnerProfileEntity profile = profileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("MachineryOwnerProfile", "userId", currentUserId));
        return mapToDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public MachineryOwnerProfileResponseDto getProfileByUserId(UUID userId) {
        MachineryOwnerProfileEntity profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MachineryOwnerProfile", "userId", userId));
        return mapToDto(profile);
    }

    private MachineryOwnerProfileResponseDto mapToDto(MachineryOwnerProfileEntity entity) {
        return new MachineryOwnerProfileResponseDto(
                entity.getId(),
                entity.getUser().getId(),
                entity.getFullName(),
                entity.getAddress(),
                entity.getContactNumber(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
