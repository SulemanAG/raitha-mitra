package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.CreateUserRequestDto;
import com.raithamitra.backend.dto.response.UserResponseDto;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.exception.ResourceAlreadyExistsException;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service Implementation executing User management business operations with transactional integrity.
 * Uses constructor dependency injection.
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

        UserEntity userEntity = UserEntity.builder()
                .mobileNumber(requestDto.mobileNumber())
                .primaryRole(requestDto.primaryRole())
                .active(true)
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

    private UserResponseDto mapToUserResponseDto(UserEntity entity) {
        return new UserResponseDto(
                entity.getId(),
                entity.getMobileNumber(),
                entity.getPrimaryRole(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String maskMobile(String mobile) {
        if (mobile != null && mobile.length() >= 10) {
            return mobile.substring(0, 5) + "*****" + mobile.substring(mobile.length() - 2);
        }
        return "*****";
    }
}
