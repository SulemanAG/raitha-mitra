package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.CreateRentalRequestDto;
import com.raithamitra.backend.dto.request.UpdateLocationRequestDto;
import com.raithamitra.backend.dto.response.MachineryDiscoveryResponseDto;
import com.raithamitra.backend.entity.LocationData;
import com.raithamitra.backend.entity.LocationSource;
import com.raithamitra.backend.entity.MachineryCategory;
import com.raithamitra.backend.entity.MachineryEntity;
import com.raithamitra.backend.entity.OperationalStatus;
import com.raithamitra.backend.entity.RentalRequestEntity;
import com.raithamitra.backend.entity.RentalStatus;
import com.raithamitra.backend.entity.RentalUnit;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.exception.InvalidCoordinatesException;
import com.raithamitra.backend.exception.RentalPeriodConflictException;
import com.raithamitra.backend.repository.MachineryOwnerProfileRepository;
import com.raithamitra.backend.repository.MachineryRepository;
import com.raithamitra.backend.repository.RentalRequestRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.SecurityUser;
import com.raithamitra.backend.service.impl.LocationServiceImpl;
import com.raithamitra.backend.service.impl.RentalRequestServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
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
import static org.mockito.Mockito.when;

/**
 * Comprehensive Unit & Integration Test verifying Location & Discovery engineering invariants.
 * Covers coordinate validation, spatial radius filtering, privacy guarantees, optimistic locking,
 * ownership authorization, bounded pagination, and discovery-rental integration.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private MachineryRepository machineryRepository;

    @Mock
    private RentalRequestRepository rentalRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MachineryOwnerProfileRepository profileRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @InjectMocks
    private RentalRequestServiceImpl rentalRequestService;

    private UserEntity ownerA;
    private UserEntity ownerB;
    private UserEntity farmerUser;
    private MachineryEntity machineryA;
    private MachineryEntity machineryB;

    // Center point: Mandya Town (12.5224, 76.8974)
    private final double centerLat = 12.5224;
    private final double centerLon = 76.8974;

    @BeforeEach
    void setUp() {
        ownerA = UserEntity.builder().mobileNumber("+919111111111").primaryRole("MACHINERY_OWNER").roles(Set.of(UserRole.MACHINERY_OWNER)).build();
        ownerA.setId(UUID.randomUUID());

        ownerB = UserEntity.builder().mobileNumber("+919222222222").primaryRole("MACHINERY_OWNER").roles(Set.of(UserRole.MACHINERY_OWNER)).build();
        ownerB.setId(UUID.randomUUID());

        farmerUser = UserEntity.builder().mobileNumber("+919333333333").primaryRole("FARMER").roles(Set.of(UserRole.FARMER)).build();
        farmerUser.setId(UUID.randomUUID());

        // Machinery A: Mandya (~0 km away)
        machineryA = new MachineryEntity();
        machineryA.setId(UUID.randomUUID());
        machineryA.setOwnerUser(ownerA);
        machineryA.setName("Mandya Tractor");
        machineryA.setCategory(MachineryCategory.TRACTOR);
        machineryA.setDailyRate(new BigDecimal("2000.00"));
        machineryA.setStatus(OperationalStatus.ACTIVE);
        machineryA.setVersion(1L);
        machineryA.setLocationData(new LocationData(12.5224, 76.8974, "Karnataka", "Mandya", "Mandya", "Mandya Town", LocationSource.DEVICE));

        // Machinery B: Mysuru (~40 km away)
        machineryB = new MachineryEntity();
        machineryB.setId(UUID.randomUUID());
        machineryB.setOwnerUser(ownerB);
        machineryB.setName("Mysuru Harvester");
        machineryB.setCategory(MachineryCategory.HARVESTER);
        machineryB.setDailyRate(new BigDecimal("5000.00"));
        machineryB.setStatus(OperationalStatus.ACTIVE);
        machineryB.setVersion(1L);
        machineryB.setLocationData(new LocationData(12.2958, 76.6394, "Karnataka", "Mysuru", "Mysuru", "Mysuru City", LocationSource.DEVICE));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // TEST 1 — Coordinate validation
    @Test
    void test1_coordinateValidation_ShouldRejectInvalidLatLon() {
        assertThatThrownBy(() -> locationService.validateCoordinates(91.0, 76.0))
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessageContaining("Coordinates out of range");

        assertThatThrownBy(() -> locationService.validateCoordinates(-91.0, 76.0))
                .isInstanceOf(InvalidCoordinatesException.class);

        assertThatThrownBy(() -> locationService.validateCoordinates(12.0, 181.0))
                .isInstanceOf(InvalidCoordinatesException.class);

        assertThatThrownBy(() -> locationService.validateCoordinates(12.0, null))
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessageContaining("provided together");

        assertThatThrownBy(() -> locationService.validateCoordinates(Double.NaN, 76.0))
                .isInstanceOf(InvalidCoordinatesException.class);
    }

    // TEST 2 — Nearby discovery (Inside vs Outside radius)
    @Test
    void test2_nearbyDiscovery_ShouldFilterByRadius() {
        when(machineryRepository.findAll()).thenReturn(List.of(machineryA, machineryB));

        // Search with 20 km radius around Mandya center
        Page<MachineryDiscoveryResponseDto> page = locationService.discoverNearbyMachinery(
                centerLat, centerLon, 20.0, null, null, null, PageRequest.of(0, 10)
        );

        List<MachineryDiscoveryResponseDto> results = page.getContent();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Mandya Tractor");
    }

    // TEST 3 — Boundary / radius correctness
    @Test
    void test3_boundaryRadius_ShouldCalculateExactDistance() {
        when(machineryRepository.findAll()).thenReturn(List.of(machineryA, machineryB));

        // Search with 50 km radius: should include both Machinery A (~0 km) and Machinery B (~40 km)
        Page<MachineryDiscoveryResponseDto> page = locationService.discoverNearbyMachinery(
                centerLat, centerLon, 50.0, null, null, null, PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(2);
    }

    // TEST 4 — Distance ordering
    @Test
    void test4_distanceOrdering_ShouldSortNearestToFarthest() {
        when(machineryRepository.findAll()).thenReturn(List.of(machineryB, machineryA)); // Reversed order in repo

        Page<MachineryDiscoveryResponseDto> page = locationService.discoverNearbyMachinery(
                centerLat, centerLon, 50.0, null, null, null, PageRequest.of(0, 10)
        );

        List<MachineryDiscoveryResponseDto> results = page.getContent();
        assertThat(results.get(0).name()).isEqualTo("Mandya Tractor"); // ~0 km
        assertThat(results.get(1).name()).isEqualTo("Mysuru Harvester"); // ~40 km
        assertThat(results.get(0).approximateDistanceKm()).isLessThan(results.get(1).approximateDistanceKm());
    }

    // TEST 5 — Privacy (Does NOT expose exact coordinates)
    @Test
    void test5_privacy_ShouldNotExposeExactCoordinates() throws Exception {
        MachineryDiscoveryResponseDto dto = new MachineryDiscoveryResponseDto(
                machineryA.getId(),
                ownerA.getId(),
                "Owner Name",
                "Mandya Tractor",
                "Model X",
                "TRACTOR",
                50,
                "Mandya",
                "Mandya",
                "Mandya Town",
                0.5,
                new BigDecimal("2000.00"),
                null,
                "ACTIVE",
                true
        );

        // Assert reflection check: DTO class must not contain latitude or longitude fields
        Field[] fields = MachineryDiscoveryResponseDto.class.getDeclaredFields();
        for (Field f : fields) {
            assertThat(f.getName()).isNotEqualTo("latitude");
            assertThat(f.getName()).isNotEqualTo("longitude");
        }
        assertThat(dto.approximateDistanceKm()).isEqualTo(0.5);
    }

    // TEST 6 — Ownership authorization (Owner B cannot update Owner A's machine location)
    @Test
    void test6_ownershipAuthorization_ShouldPreventCrossOwnerLocationUpdate() {
        setSecurityContext(ownerB); // Authenticated as Owner B

        UpdateLocationRequestDto requestDto = new UpdateLocationRequestDto(
                new BigDecimal("12.5224"),
                new BigDecimal("76.8974"),
                "Karnataka",
                "Mandya",
                "Mandya",
                "Village",
                "DEVICE",
                1L
        );

        when(machineryRepository.findById(machineryA.getId())).thenReturn(Optional.of(machineryA));

        assertThatThrownBy(() -> locationService.updateMachineryLocation(machineryA.getId(), requestDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User is not authorized");
    }

    // TEST 7 — Non-owner cannot modify another resource
    @Test
    void test7_nonOwner_ShouldBeForbiddenFromUpdatingMachineryLocation() {
        setSecurityContext(farmerUser); // Authenticated as Farmer

        UpdateLocationRequestDto requestDto = new UpdateLocationRequestDto(
                new BigDecimal("12.5224"),
                new BigDecimal("76.8974"),
                "Karnataka",
                "Mandya",
                "Mandya",
                "Village",
                "DEVICE",
                1L
        );

        when(machineryRepository.findById(machineryA.getId())).thenReturn(Optional.of(machineryA));

        assertThatThrownBy(() -> locationService.updateMachineryLocation(machineryA.getId(), requestDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    // TEST 8 — Operational status filtering (Only ACTIVE assets discoverable)
    @Test
    void test8_operationalStatus_ShouldOnlyDiscoverActiveMachinery() {
        machineryA.setStatus(OperationalStatus.UNDER_MAINTENANCE);
        when(machineryRepository.findAll()).thenReturn(List.of(machineryA, machineryB));

        Page<MachineryDiscoveryResponseDto> page = locationService.discoverNearbyMachinery(
                centerLat, centerLon, 50.0, null, null, null, PageRequest.of(0, 10)
        );

        List<MachineryDiscoveryResponseDto> results = page.getContent();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Mysuru Harvester");
    }

    // TEST 9 — Pagination limit (Clamps requested large page size)
    @Test
    void test9_paginationLimit_ShouldCapMaxPageSize() {
        when(machineryRepository.findAll()).thenReturn(List.of(machineryA, machineryB));

        // Request page size 500 (exceeds MAX_PAGE_SIZE 50)
        Page<MachineryDiscoveryResponseDto> page = locationService.discoverNearbyMachinery(
                centerLat, centerLon, 50.0, null, null, null, PageRequest.of(0, 500)
        );

        assertThat(page.getPageable().getPageSize()).isEqualTo(50);
    }

    // TEST 10 — Stale update / optimistic locking
    @Test
    void test10_optimisticLocking_ShouldThrowConflictOnStaleUpdate() {
        setSecurityContext(ownerA);

        UpdateLocationRequestDto requestDto = new UpdateLocationRequestDto(
                new BigDecimal("12.5224"),
                new BigDecimal("76.8974"),
                "Karnataka",
                "Mandya",
                "Mandya",
                "Village",
                "DEVICE",
                0L // Stale version 0L (Current is 1L)
        );

        when(machineryRepository.findById(machineryA.getId())).thenReturn(Optional.of(machineryA));

        assertThatThrownBy(() -> locationService.updateMachineryLocation(machineryA.getId(), requestDto))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    // TEST 11 — Discovery + rental integration (Booked dates mark availableForDates = false)
    @Test
    void test11_discoveryRentalIntegration_ShouldHideBookedDates() {
        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(5);

        when(machineryRepository.findAll()).thenReturn(List.of(machineryA));
        when(rentalRequestRepository.existsOverlappingAcceptedBooking(eq(machineryA.getId()), eq(start), eq(end), any()))
                .thenReturn(true); // Conflicting accepted booking exists

        Page<MachineryDiscoveryResponseDto> page = locationService.discoverNearbyMachinery(
                centerLat, centerLon, 50.0, null, start, end, PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).availableForDates()).isFalse();
    }

    // TEST 12 — Stale discovery does not bypass booking protection
    @Test
    void test12_staleDiscovery_ShouldNotBypassBookingProtection() {
        setSecurityContext(farmerUser);

        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end = LocalDate.now().plusDays(5);

        CreateRentalRequestDto createDto = new CreateRentalRequestDto(
                machineryA.getId(),
                start,
                end,
                "DAILY",
                3,
                "Notes"
        );

        when(userRepository.findById(farmerUser.getId())).thenReturn(Optional.of(farmerUser));
        when(machineryRepository.findById(machineryA.getId())).thenReturn(Optional.of(machineryA));
        when(rentalRequestRepository.existsOverlappingAcceptedBooking(eq(machineryA.getId()), eq(start), eq(end), any()))
                .thenReturn(true); // Another user booked it first!

        assertThatThrownBy(() -> rentalRequestService.createRentalRequest(createDto))
                .isInstanceOf(RentalPeriodConflictException.class)
                .hasMessageContaining("Selected dates overlap with an existing confirmed booking");
    }

    private void setSecurityContext(UserEntity user) {
        SecurityUser securityUser = SecurityUser.fromEntity(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
