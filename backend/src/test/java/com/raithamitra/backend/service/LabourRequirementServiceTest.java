package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateLabourRequirementRequestDto;
import com.raithamitra.backend.dto.request.UpdateLabourRequirementRequestDto;
import com.raithamitra.backend.dto.response.LabourRequirementResponseDto;
import com.raithamitra.backend.entity.LabourRequirementEntity;
import com.raithamitra.backend.entity.RequirementStatus;
import com.raithamitra.backend.entity.TaskType;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.repository.LabourRequirementRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.SecurityUser;
import com.raithamitra.backend.service.impl.LabourRequirementServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Test verifying LabourRequirementServiceImpl business logic and ownership authorization.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class LabourRequirementServiceTest {

    @Mock
    private LabourRequirementRepository labourRequirementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LabourRequirementServiceImpl labourRequirementService;

    private UUID farmerUserId;
    private UserEntity farmerUser;
    private LabourRequirementEntity sampleRequirement;

    @BeforeEach
    void setUp() {
        farmerUserId = UUID.randomUUID();

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.FARMER);

        farmerUser = UserEntity.builder()
                .mobileNumber("+919876543210")
                .primaryRole("FARMER")
                .roles(roles)
                .build();
        farmerUser.setId(farmerUserId);

        SecurityUser securityUser = SecurityUser.fromEntity(farmerUser);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        sampleRequirement = new LabourRequirementEntity();
        sampleRequirement.setId(UUID.randomUUID());
        sampleRequirement.setFarmerUser(farmerUser);
        sampleRequirement.setTitle("Sugarcane Harvesting");
        sampleRequirement.setTaskType(TaskType.HARVESTING);
        sampleRequirement.setLocation("Mandya");
        sampleRequirement.setRequiredWorkersCount(5);
        sampleRequirement.setDailyOfferedWage(new BigDecimal("600.00"));
        sampleRequirement.setWorkDate(LocalDate.now().plusDays(2));
        sampleRequirement.setStatus(RequirementStatus.OPEN);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createRequirement_ShouldSaveAndReturnDto() {
        CreateLabourRequirementRequestDto requestDto = new CreateLabourRequirementRequestDto(
                "Sugarcane Harvesting",
                "Need 5 workers for harvesting",
                "HARVESTING",
                "Mandya",
                5,
                new BigDecimal("600.00"),
                LocalDate.now().plusDays(2),
                Set.of("HARVESTING")
        );

        when(userRepository.findById(farmerUserId)).thenReturn(Optional.of(farmerUser));
        when(labourRequirementRepository.save(any(LabourRequirementEntity.class))).thenReturn(sampleRequirement);

        LabourRequirementResponseDto result = labourRequirementService.createRequirement(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Sugarcane Harvesting");
        assertThat(result.location()).isEqualTo("Mandya");
        verify(labourRequirementRepository).save(any(LabourRequirementEntity.class));
    }

    @Test
    void updateRequirement_ShouldThrowAccessDeniedException_WhenUserIsNotOwner() {
        UUID otherUserId = UUID.randomUUID();
        UserEntity otherUser = UserEntity.builder().mobileNumber("+919111111111").primaryRole("FARMER").build();
        otherUser.setId(otherUserId);

        LabourRequirementEntity requirementOwnedByOther = new LabourRequirementEntity();
        requirementOwnedByOther.setId(UUID.randomUUID());
        requirementOwnedByOther.setFarmerUser(otherUser);

        UpdateLabourRequirementRequestDto updateDto = new UpdateLabourRequirementRequestDto(
                "Updated Title",
                "Desc",
                "HARVESTING",
                "Mandya",
                5,
                new BigDecimal("600.00"),
                LocalDate.now().plusDays(3),
                Set.of()
        );

        when(labourRequirementRepository.findById(requirementOwnedByOther.getId())).thenReturn(Optional.of(requirementOwnedByOther));

        assertThatThrownBy(() -> labourRequirementService.updateRequirement(requirementOwnedByOther.getId(), updateDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User is not authorized");
    }
}
