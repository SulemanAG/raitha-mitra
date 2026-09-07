package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.CreateUserRequestDto;
import com.raithamitra.backend.dto.request.UpdateUserStatusRequestDto;
import com.raithamitra.backend.dto.response.FarmerProfileResponseDto;
import com.raithamitra.backend.dto.response.LabourerProfileResponseDto;
import com.raithamitra.backend.dto.response.UserProfileResponseDto;
import com.raithamitra.backend.dto.response.UserResponseDto;
import com.raithamitra.backend.entity.AccountStatus;
import com.raithamitra.backend.entity.FarmerProfileEntity;
import com.raithamitra.backend.entity.LabourerProfileEntity;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.exception.ResourceAlreadyExistsException;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation executing User management business operations with transactional integrity.
 * Supports multi-role mapping, composite profile creation, and account status lifecycle management.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto requestDto) {
        log.info("Attempting to create user with mobile number: {}", maskMobile(requestDto.mobileNumber()));

        if (userRepository.existsByMobileNumber(requestDto.mobileNumber())) {
            throw new ResourceAlreadyExistsException("User", "mobileNumber", requestDto.mobileNumber());
        }

        UserRole primaryRoleEnum = parseRole(requestDto.primaryRole());
        Set<UserRole> roles = new HashSet<>();
        roles.add(primaryRoleEnum);

        UserEntity userEntity = UserEntity.builder()
                .mobileNumber(requestDto.mobileNumber())
                .primaryRole(requestDto.primaryRole())
                .accountStatus(AccountStatus.ACTIVE)
                .roles(roles)
                .build();

        UserEntity savedEntity = userRepository.save(userEntity);
        log.info("Successfully created user with ID: {}", savedEntity.getId());
        return mapToUserResponseDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToUserResponseDto(userEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfileById(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        FarmerProfileResponseDto farmerProfileDto = null;
        if (userEntity.getFarmerProfile() != null) {
            FarmerProfileEntity fp = userEntity.getFarmerProfile();
            farmerProfileDto = new FarmerProfileResponseDto(
                    fp.getId(),
                    userEntity.getId(),
                    fp.getFullName(),
                    fp.getFarmLocation(),
                    fp.getFarmSizeAcres(),
                    fp.getPreferredCropTypes(),
                    fp.getCreatedAt(),
                    fp.getUpdatedAt()
            );
        }

        LabourerProfileResponseDto labourerProfileDto = null;
        if (userEntity.getLabourerProfile() != null) {
            LabourerProfileEntity lp = userEntity.getLabourerProfile();
            Set<String> skills = lp.getSkills().stream().map(Enum::name).collect(Collectors.toSet());
            labourerProfileDto = new LabourerProfileResponseDto(
                    lp.getId(),
                    userEntity.getId(),
                    lp.getFullName(),
                    lp.getDailyWageRate(),
                    lp.getExperienceYears(),
                    lp.getAvailabilityStatus().name(),
                    skills,
                    lp.getCreatedAt(),
                    lp.getUpdatedAt()
            );
        }

        Set<String> roleNames = userEntity.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

        return new UserProfileResponseDto(
                userEntity.getId(),
                userEntity.getMobileNumber(),
                userEntity.getPrimaryRole(),
                userEntity.getAccountStatus().name(),
                roleNames,
                farmerProfileDto,
                labourerProfileDto,
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByMobileNumber(String mobileNumber) {
        UserEntity userEntity = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User", "mobileNumber", mobileNumber));
        return mapToUserResponseDto(userEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto updateUserStatus(UUID id, UpdateUserStatusRequestDto requestDto) {
        log.info("Updating account status for user ID: {} to {}", id, requestDto.accountStatus());

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        AccountStatus newStatus = AccountStatus.valueOf(requestDto.accountStatus().toUpperCase().trim());
        userEntity.setAccountStatus(newStatus);

        UserEntity savedEntity = userRepository.save(userEntity);
        return mapToUserResponseDto(savedEntity);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        log.info("Deactivating user account ID: {}", id);

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userEntity.setAccountStatus(AccountStatus.DEACTIVATED);
        userRepository.save(userEntity);
    }

    private UserResponseDto mapToUserResponseDto(UserEntity entity) {
        Set<String> roleNames = entity.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new UserResponseDto(
                entity.getId(),
                entity.getMobileNumber(),
                entity.getPrimaryRole(),
                entity.getAccountStatus().name(),
                roleNames,
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private UserRole parseRole(String roleStr) {
        try {
            return UserRole.valueOf(roleStr.toUpperCase().trim());
        } catch (Exception e) {
            return UserRole.FARMER;
        }
    }

    private String maskMobile(String mobile) {
        if (mobile != null && mobile.length() >= 10) {
            return mobile.substring(0, 5) + "*****" + mobile.substring(mobile.length() - 2);
        }
        return "*****";
    }
}
