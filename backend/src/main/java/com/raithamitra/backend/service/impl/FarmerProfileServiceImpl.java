package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.CreateFarmerProfileRequestDto;
import com.raithamitra.backend.dto.request.UpdateFarmerProfileRequestDto;
import com.raithamitra.backend.dto.response.FarmerProfileResponseDto;
import com.raithamitra.backend.entity.FarmerProfileEntity;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.exception.ResourceAlreadyExistsException;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.FarmerProfileRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.service.FarmerProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service Implementation executing Farmer Profile business rules with transactional consistency.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class FarmerProfileServiceImpl implements FarmerProfileService {

    private static final Logger log = LoggerFactory.getLogger(FarmerProfileServiceImpl.class);

    private final FarmerProfileRepository farmerProfileRepository;
    private final UserRepository userRepository;

    public FarmerProfileServiceImpl(FarmerProfileRepository farmerProfileRepository, UserRepository userRepository) {
        this.farmerProfileRepository = farmerProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public FarmerProfileResponseDto createFarmerProfile(CreateFarmerProfileRequestDto requestDto) {
        log.info("Attempting to create Farmer profile for user ID: {}", requestDto.userId());

        UserEntity userEntity = userRepository.findById(requestDto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.userId()));

        if (farmerProfileRepository.existsByUserId(requestDto.userId())) {
            throw new ResourceAlreadyExistsException("FarmerProfile", "userId", requestDto.userId());
        }

        // Add FARMER role to user if not present
        userEntity.getRoles().add(UserRole.FARMER);

        FarmerProfileEntity profileEntity = new FarmerProfileEntity(
                userEntity,
                requestDto.fullName(),
                requestDto.farmLocation(),
                requestDto.farmSizeAcres(),
                requestDto.preferredCropTypes()
        );

        userEntity.setFarmerProfile(profileEntity);
        userRepository.save(userEntity);

        FarmerProfileEntity savedProfile = farmerProfileRepository.findByUserId(requestDto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("FarmerProfile", "userId", requestDto.userId()));

        log.info("Successfully created Farmer profile ID: {} for user ID: {}", savedProfile.getId(), requestDto.userId());
        return mapToFarmerProfileResponseDto(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerProfileResponseDto getFarmerProfileByUserId(UUID userId) {
        FarmerProfileEntity profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("FarmerProfile", "userId", userId));
        return mapToFarmerProfileResponseDto(profile);
    }

    @Override
    @Transactional
    public FarmerProfileResponseDto updateFarmerProfile(UUID userId, UpdateFarmerProfileRequestDto requestDto) {
        log.info("Updating Farmer profile for user ID: {}", userId);

        FarmerProfileEntity profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("FarmerProfile", "userId", userId));

        profile.setFullName(requestDto.fullName());
        profile.setFarmLocation(requestDto.farmLocation());
        profile.setFarmSizeAcres(requestDto.farmSizeAcres());
        profile.setPreferredCropTypes(requestDto.preferredCropTypes());

        FarmerProfileEntity updatedProfile = farmerProfileRepository.save(profile);
        return mapToFarmerProfileResponseDto(updatedProfile);
    }

    private FarmerProfileResponseDto mapToFarmerProfileResponseDto(FarmerProfileEntity entity) {
        return new FarmerProfileResponseDto(
                entity.getId(),
                entity.getUser().getId(),
                entity.getFullName(),
                entity.getFarmLocation(),
                entity.getFarmSizeAcres(),
                entity.getPreferredCropTypes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
