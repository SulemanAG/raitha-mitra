package com.raithamitra.backend.service;

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
import com.raithamitra.backend.service.impl.FarmerProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Test verifying FarmerProfileServiceImpl business logic and validation rules.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class FarmerProfileServiceTest {

    @Mock
    private FarmerProfileRepository farmerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FarmerProfileServiceImpl farmerProfileService;

    private UUID userId;
    private UUID profileId;
    private UserEntity sampleUser;
    private FarmerProfileEntity sampleProfile;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        sampleUser = UserEntity.builder()
                .mobileNumber("+919876543210")
                .primaryRole("FARMER")
                .roles(new HashSet<>())
                .build();
        sampleUser.setId(userId);

        sampleProfile = new FarmerProfileEntity(
                sampleUser,
                "Ramesh Kumar",
                "Mandya, Karnataka",
                new BigDecimal("5.50"),
                "Paddy, Sugarcane"
        );
        sampleProfile.setId(profileId);
        sampleProfile.setCreatedAt(Instant.now());
        sampleProfile.setUpdatedAt(Instant.now());
    }

    @Test
    void createFarmerProfile_ShouldSaveAndReturnDto_WhenValid() {
        CreateFarmerProfileRequestDto requestDto = new CreateFarmerProfileRequestDto(
                userId,
                "Ramesh Kumar",
                "Mandya, Karnataka",
                new BigDecimal("5.50"),
                "Paddy, Sugarcane"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(farmerProfileRepository.existsByUserId(userId)).thenReturn(false);
        when(farmerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(sampleProfile));

        FarmerProfileResponseDto result = farmerProfileService.createFarmerProfile(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo("Ramesh Kumar");
        assertThat(result.farmLocation()).isEqualTo("Mandya, Karnataka");
        assertThat(sampleUser.getRoles()).contains(UserRole.FARMER);
        verify(userRepository).save(sampleUser);
    }

    @Test
    void createFarmerProfile_ShouldThrowResourceAlreadyExistsException_WhenProfileExists() {
        CreateFarmerProfileRequestDto requestDto = new CreateFarmerProfileRequestDto(
                userId,
                "Ramesh Kumar",
                "Mandya, Karnataka",
                new BigDecimal("5.50"),
                "Paddy"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(farmerProfileRepository.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> farmerProfileService.createFarmerProfile(requestDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void getFarmerProfileByUserId_ShouldReturnProfile_WhenExists() {
        when(farmerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(sampleProfile));

        FarmerProfileResponseDto result = farmerProfileService.getFarmerProfileByUserId(userId);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
    }

    @Test
    void getFarmerProfileByUserId_ShouldThrowResourceNotFoundException_WhenDoesNotExist() {
        when(farmerProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> farmerProfileService.getFarmerProfileByUserId(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
