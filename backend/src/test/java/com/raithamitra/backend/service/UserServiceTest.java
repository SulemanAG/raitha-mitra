package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateUserRequestDto;
import com.raithamitra.backend.dto.response.UserResponseDto;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.exception.ResourceAlreadyExistsException;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Test verifying UserServiceImpl business logic using Mockito test doubles.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserRequestDto requestDto;
    private UserEntity sampleUserEntity;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        requestDto = new CreateUserRequestDto("+919876543210", "FARMER");

        sampleUserEntity = UserEntity.builder()
                .mobileNumber("+919876543210")
                .primaryRole("FARMER")
                .active(true)
                .build();
        sampleUserEntity.setId(sampleId);
        sampleUserEntity.setCreatedAt(Instant.now());
        sampleUserEntity.setUpdatedAt(Instant.now());
    }

    @Test
    void createUser_ShouldSaveAndReturnDto_WhenMobileIsNew() {
        when(userRepository.existsByMobileNumber(requestDto.mobileNumber())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(sampleUserEntity);

        UserResponseDto result = userService.createUser(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(sampleId);
        assertThat(result.mobileNumber()).isEqualTo("+919876543210");
        assertThat(result.primaryRole()).isEqualTo("FARMER");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_ShouldThrowResourceAlreadyExistsException_WhenMobileAlreadyExists() {
        when(userRepository.existsByMobileNumber(requestDto.mobileNumber())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findById(sampleId)).thenReturn(Optional.of(sampleUserEntity));

        UserResponseDto result = userService.getUserById(sampleId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(sampleId);
    }

    @Test
    void getUserById_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(sampleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(sampleId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
