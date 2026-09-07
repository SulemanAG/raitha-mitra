package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateMachineryRequestDto;
import com.raithamitra.backend.dto.request.UpdateMachineryRequestDto;
import com.raithamitra.backend.dto.response.MachineryResponseDto;
import com.raithamitra.backend.entity.MachineryCategory;
import com.raithamitra.backend.entity.MachineryEntity;
import com.raithamitra.backend.entity.OperationalStatus;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.repository.MachineryOwnerProfileRepository;
import com.raithamitra.backend.repository.MachineryRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.SecurityUser;
import com.raithamitra.backend.service.impl.MachineryServiceImpl;
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
 * Unit Test verifying MachineryServiceImpl business logic and ownership authorization rules.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class MachineryServiceTest {

    @Mock
    private MachineryRepository machineryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MachineryOwnerProfileRepository profileRepository;

    @InjectMocks
    private MachineryServiceImpl machineryService;

    private UUID ownerUserId;
    private UserEntity ownerUser;
    private MachineryEntity sampleMachinery;

    @BeforeEach
    void setUp() {
        ownerUserId = UUID.randomUUID();

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.MACHINERY_OWNER);

        ownerUser = UserEntity.builder()
                .mobileNumber("+919876543210")
                .primaryRole("MACHINERY_OWNER")
                .roles(roles)
                .build();
        ownerUser.setId(ownerUserId);

        SecurityUser securityUser = SecurityUser.fromEntity(ownerUser);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        sampleMachinery = new MachineryEntity();
        sampleMachinery.setId(UUID.randomUUID());
        sampleMachinery.setOwnerUser(ownerUser);
        sampleMachinery.setName("John Deere Tractor");
        sampleMachinery.setCategory(MachineryCategory.TRACTOR);
        sampleMachinery.setHpRating(50);
        sampleMachinery.setLocation("Mandya");
        sampleMachinery.setDailyRate(new BigDecimal("2500.00"));
        sampleMachinery.setStatus(OperationalStatus.ACTIVE);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerMachinery_ShouldSaveAndReturnDto() {
        CreateMachineryRequestDto requestDto = new CreateMachineryRequestDto(
                "John Deere Tractor",
                "5050D",
                "TRACTOR",
                50,
                "Mandya",
                new BigDecimal("2500.00"),
                new BigDecimal("350.00")
        );

        when(userRepository.findById(ownerUserId)).thenReturn(Optional.of(ownerUser));
        when(machineryRepository.save(any(MachineryEntity.class))).thenReturn(sampleMachinery);
        when(profileRepository.findByUserId(ownerUserId)).thenReturn(Optional.empty());

        MachineryResponseDto result = machineryService.registerMachinery(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("John Deere Tractor");
        assertThat(result.category()).isEqualTo("TRACTOR");
        verify(machineryRepository).save(any(MachineryEntity.class));
    }

    @Test
    void updateMachinery_ShouldThrowAccessDeniedException_WhenUserIsNotOwner() {
        UUID otherUserId = UUID.randomUUID();
        UserEntity otherUser = UserEntity.builder().mobileNumber("+919111111111").primaryRole("MACHINERY_OWNER").build();
        otherUser.setId(otherUserId);

        MachineryEntity machineryOwnedByOther = new MachineryEntity();
        machineryOwnedByOther.setId(UUID.randomUUID());
        machineryOwnedByOther.setOwnerUser(otherUser);

        UpdateMachineryRequestDto updateDto = new UpdateMachineryRequestDto(
                "Updated Name",
                "Model",
                "TRACTOR",
                50,
                "Mandya",
                new BigDecimal("3000.00"),
                null,
                "ACTIVE"
        );

        when(machineryRepository.findById(machineryOwnedByOther.getId())).thenReturn(Optional.of(machineryOwnedByOther));

        assertThatThrownBy(() -> machineryService.updateMachinery(machineryOwnedByOther.getId(), updateDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User is not authorized");
    }
}
