package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateLabourerProfileRequestDto;
import com.raithamitra.backend.dto.response.LabourerProfileResponseDto;
import com.raithamitra.backend.entity.AvailabilityStatus;
import com.raithamitra.backend.entity.LabourSkill;
import com.raithamitra.backend.entity.LabourerProfileEntity;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.repository.LabourerProfileRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.service.impl.LabourerProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Test verifying LabourerProfileServiceImpl business logic and workforce discovery.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class LabourerProfileServiceTest {

    @Mock
    private LabourerProfileRepository labourerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LabourerProfileServiceImpl labourerProfileService;

    private UUID userId;
    private UUID profileId;
    private UserEntity sampleUser;
    private LabourerProfileEntity sampleProfile;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        sampleUser = UserEntity.builder()
                .mobileNumber("+919876543210")
                .primaryRole("LABOURER")
                .roles(new HashSet<>())
                .build();
        sampleUser.setId(userId);

        Set<LabourSkill> skills = Set.of(LabourSkill.HARVESTING, LabourSkill.SOWING);
        sampleProfile = new LabourerProfileEntity(
                sampleUser,
                "Suresh Gowda",
                new BigDecimal("600.00"),
                5,
                AvailabilityStatus.AVAILABLE,
                skills
        );
        sampleProfile.setId(profileId);
        sampleProfile.setCreatedAt(Instant.now());
        sampleProfile.setUpdatedAt(Instant.now());
    }

    @Test
    void createLabourerProfile_ShouldSaveAndReturnDto_WhenValid() {
        CreateLabourerProfileRequestDto requestDto = new CreateLabourerProfileRequestDto(
                userId,
                "Suresh Gowda",
                new BigDecimal("600.00"),
                5,
                "AVAILABLE",
                Set.of("HARVESTING", "SOWING")
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(labourerProfileRepository.existsByUserId(userId)).thenReturn(false);
        when(labourerProfileRepository.findByUserId(userId)).thenReturn(Optional.of(sampleProfile));

        LabourerProfileResponseDto result = labourerProfileService.createLabourerProfile(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo("Suresh Gowda");
        assertThat(result.skills()).contains("HARVESTING", "SOWING");
        assertThat(sampleUser.getRoles()).contains(UserRole.LABOURER);
        verify(userRepository).save(sampleUser);
    }

    @Test
    void searchLabourers_ShouldReturnPaginatedResults_WhenSkillProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LabourerProfileEntity> entityPage = new PageImpl<>(List.of(sampleProfile));

        when(labourerProfileRepository.findByAvailabilityAndSkill(AvailabilityStatus.AVAILABLE, LabourSkill.HARVESTING, pageable))
                .thenReturn(entityPage);

        Page<LabourerProfileResponseDto> result = labourerProfileService.searchLabourers("AVAILABLE", "HARVESTING", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).fullName()).isEqualTo("Suresh Gowda");
    }
}
