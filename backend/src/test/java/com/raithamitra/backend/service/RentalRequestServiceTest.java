package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateRentalRequestDto;
import com.raithamitra.backend.dto.response.RentalRequestResponseDto;
import com.raithamitra.backend.entity.MachineryCategory;
import com.raithamitra.backend.entity.MachineryEntity;
import com.raithamitra.backend.entity.OperationalStatus;
import com.raithamitra.backend.entity.RentalRequestEntity;
import com.raithamitra.backend.entity.RentalStatus;
import com.raithamitra.backend.entity.RentalUnit;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.exception.RentalPeriodConflictException;
import com.raithamitra.backend.repository.MachineryRepository;
import com.raithamitra.backend.repository.RentalRequestRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.SecurityUser;
import com.raithamitra.backend.service.impl.RentalRequestServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Test verifying RentalRequestServiceImpl logic, pessimistic locking approval flow,
 * and double-booking conflict prevention.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class RentalRequestServiceTest {

    @Mock
    private RentalRequestRepository rentalRequestRepository;

    @Mock
    private MachineryRepository machineryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RentalRequestServiceImpl rentalRequestService;

    private UUID renterUserId;
    private UUID ownerUserId;
    private UserEntity renterUser;
    private UserEntity ownerUser;
    private MachineryEntity sampleMachinery;
    private RentalRequestEntity sampleRentalRequest;

    @BeforeEach
    void setUp() {
        renterUserId = UUID.randomUUID();
        ownerUserId = UUID.randomUUID();

        renterUser = UserEntity.builder().mobileNumber("+919876543210").primaryRole("FARMER").roles(Set.of(UserRole.FARMER)).build();
        renterUser.setId(renterUserId);

        ownerUser = UserEntity.builder().mobileNumber("+919111111111").primaryRole("MACHINERY_OWNER").roles(Set.of(UserRole.MACHINERY_OWNER)).build();
        ownerUser.setId(ownerUserId);

        sampleMachinery = new MachineryEntity();
        sampleMachinery.setId(UUID.randomUUID());
        sampleMachinery.setOwnerUser(ownerUser);
        sampleMachinery.setName("Kubota Combine Harvester");
        sampleMachinery.setCategory(MachineryCategory.HARVESTER);
        sampleMachinery.setDailyRate(new BigDecimal("5000.00"));
        sampleMachinery.setStatus(OperationalStatus.ACTIVE);

        sampleRentalRequest = new RentalRequestEntity();
        sampleRentalRequest.setId(UUID.randomUUID());
        sampleRentalRequest.setMachinery(sampleMachinery);
        sampleRentalRequest.setRenterUser(renterUser);
        sampleRentalRequest.setStartDate(LocalDate.now().plusDays(2));
        sampleRentalRequest.setEndDate(LocalDate.now().plusDays(4));
        sampleRentalRequest.setRentalUnit(RentalUnit.DAILY);
        sampleRentalRequest.setEstimatedUnits(3);
        sampleRentalRequest.setRatePerUnit(new BigDecimal("5000.00"));
        sampleRentalRequest.setTotalAmount(new BigDecimal("15000.00"));
        sampleRentalRequest.setStatus(RentalStatus.PENDING);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createRentalRequest_ShouldSave_WhenValidAndNoOverlap() {
        setSecurityContext(renterUser);

        CreateRentalRequestDto requestDto = new CreateRentalRequestDto(
                sampleMachinery.getId(),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4),
                "DAILY",
                3,
                "Need harvester for paddy field"
        );

        when(userRepository.findById(renterUserId)).thenReturn(Optional.of(renterUser));
        when(machineryRepository.findById(sampleMachinery.getId())).thenReturn(Optional.of(sampleMachinery));
        when(rentalRequestRepository.existsOverlappingAcceptedBooking(eq(sampleMachinery.getId()), any(), any(), any())).thenReturn(false);
        when(rentalRequestRepository.save(any(RentalRequestEntity.class))).thenReturn(sampleRentalRequest);

        RentalRequestResponseDto result = rentalRequestService.createRentalRequest(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("15000.00"));
        verify(rentalRequestRepository).save(any(RentalRequestEntity.class));
    }

    @Test
    void createRentalRequest_ShouldThrowRentalPeriodConflictException_WhenDatesOverlapExistingAccepted() {
        setSecurityContext(renterUser);

        CreateRentalRequestDto requestDto = new CreateRentalRequestDto(
                sampleMachinery.getId(),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4),
                "DAILY",
                3,
                "Notes"
        );

        when(userRepository.findById(renterUserId)).thenReturn(Optional.of(renterUser));
        when(machineryRepository.findById(sampleMachinery.getId())).thenReturn(Optional.of(sampleMachinery));
        when(rentalRequestRepository.existsOverlappingAcceptedBooking(eq(sampleMachinery.getId()), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> rentalRequestService.createRentalRequest(requestDto))
                .isInstanceOf(RentalPeriodConflictException.class)
                .hasMessageContaining("Selected dates overlap with an existing confirmed booking");
    }

    @Test
    void acceptRentalRequest_ShouldMarkAcceptedAndAutoRejectOverlappingPending() {
        setSecurityContext(ownerUser);

        RentalRequestEntity conflictingPending = new RentalRequestEntity();
        conflictingPending.setId(UUID.randomUUID());
        conflictingPending.setStatus(RentalStatus.PENDING);

        when(rentalRequestRepository.findByIdForUpdate(sampleRentalRequest.getId())).thenReturn(Optional.of(sampleRentalRequest));
        when(rentalRequestRepository.existsOverlappingAcceptedBooking(eq(sampleMachinery.getId()), any(), any(), eq(sampleRentalRequest.getId()))).thenReturn(false);
        when(rentalRequestRepository.save(sampleRentalRequest)).thenReturn(sampleRentalRequest);
        when(rentalRequestRepository.findOverlappingPendingRequests(eq(sampleMachinery.getId()), any(), any(), eq(sampleRentalRequest.getId())))
                .thenReturn(List.of(conflictingPending));

        RentalRequestResponseDto result = rentalRequestService.acceptRentalRequest(sampleRentalRequest.getId(), null);

        assertThat(result).isNotNull();
        assertThat(sampleRentalRequest.getStatus()).isEqualTo(RentalStatus.ACCEPTED);
        assertThat(conflictingPending.getStatus()).isEqualTo(RentalStatus.REJECTED);
        verify(rentalRequestRepository).save(conflictingPending);
    }

    private void setSecurityContext(UserEntity user) {
        SecurityUser securityUser = SecurityUser.fromEntity(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
