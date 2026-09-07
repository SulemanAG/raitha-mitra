package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.UpdateLocationRequestDto;
import com.raithamitra.backend.dto.response.MachineryDiscoveryResponseDto;
import com.raithamitra.backend.dto.response.MachineryResponseDto;
import com.raithamitra.backend.entity.LocationData;
import com.raithamitra.backend.entity.LocationSource;
import com.raithamitra.backend.entity.MachineryCategory;
import com.raithamitra.backend.entity.MachineryEntity;
import com.raithamitra.backend.entity.OperationalStatus;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.exception.InvalidCoordinatesException;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.MachineryOwnerProfileRepository;
import com.raithamitra.backend.repository.MachineryRepository;
import com.raithamitra.backend.repository.RentalRequestRepository;
import com.raithamitra.backend.security.SecurityUtils;
import com.raithamitra.backend.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation executing spatial radius filtering, Haversine distance calculations,
 * coordinate validation, optimistic locking version enforcement, and privacy-safe discovery DTO mapping.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private static final double DEFAULT_RADIUS_KM = 25.0;
    private static final double MIN_RADIUS_KM = 1.0;
    private static final double MAX_RADIUS_KM = 100.0;
    private static final int MAX_PAGE_SIZE = 50;

    private final MachineryRepository machineryRepository;
    private final RentalRequestRepository rentalRequestRepository;
    private final MachineryOwnerProfileRepository profileRepository;

    public LocationServiceImpl(
            MachineryRepository machineryRepository,
            RentalRequestRepository rentalRequestRepository,
            MachineryOwnerProfileRepository profileRepository
    ) {
        this.machineryRepository = machineryRepository;
        this.rentalRequestRepository = rentalRequestRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null && longitude == null) {
            return;
        }

        if (latitude == null || longitude == null) {
            throw new InvalidCoordinatesException("Latitude and longitude must both be provided together");
        }

        if (Double.isNaN(latitude) || Double.isNaN(longitude) || Double.isInfinite(latitude) || Double.isInfinite(longitude)) {
            throw new InvalidCoordinatesException("Coordinates must be valid finite numbers");
        }

        if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude > 180.0) {
            throw new InvalidCoordinatesException("Coordinates out of range (-90 to +90 lat, -180 to +180 lon)");
        }
    }

    @Override
    public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371.0 * c; // Earth radius in kilometers
    }

    @Override
    @Transactional
    public MachineryResponseDto updateMachineryLocation(UUID machineryId, UpdateLocationRequestDto requestDto) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        MachineryEntity entity = machineryRepository.findById(machineryId)
                .orElseThrow(() -> new ResourceNotFoundException("Machinery", "id", machineryId));

        if (!entity.getOwnerUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User is not authorized to modify location of this machinery asset");
        }

        Double lat = requestDto.latitude() != null ? requestDto.latitude().doubleValue() : null;
        Double lon = requestDto.longitude() != null ? requestDto.longitude().doubleValue() : null;

        validateCoordinates(lat, lon);

        // Optimistic locking version check
        if (requestDto.version() != null && !requestDto.version().equals(entity.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(MachineryEntity.class, machineryId);
        }

        LocationSource sourceEnum = parseSource(requestDto.locationSource());

        LocationData locationData = new LocationData(
                lat,
                lon,
                requestDto.state(),
                requestDto.district(),
                requestDto.taluk(),
                requestDto.village(),
                sourceEnum
        );
        locationData.setLocationUpdatedAt(Instant.now());

        entity.setLocationData(locationData);
        if (StringUtils.hasText(requestDto.village()) || StringUtils.hasText(requestDto.district())) {
            String locName = (requestDto.village() != null ? requestDto.village() : "") +
                    (requestDto.district() != null ? ", " + requestDto.district() : "");
            entity.setLocation(locName.trim());
        }

        MachineryEntity saved = machineryRepository.save(entity);
        log.info("Updated location for machinery ID: {} (lat: {}, lon: {})", saved.getId(), lat, lon);
        return mapToMachineryDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineryDiscoveryResponseDto> discoverNearbyMachinery(
            Double latitude,
            Double longitude,
            Double radiusKm,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        validateCoordinates(latitude, longitude);

        // Clamp radius to [MIN_RADIUS_KM, MAX_RADIUS_KM]
        double searchRadius = radiusKm != null ? Math.min(Math.max(radiusKm, MIN_RADIUS_KM), MAX_RADIUS_KM) : DEFAULT_RADIUS_KM;

        // Bound page size to MAX_PAGE_SIZE
        int clampedSize = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Pageable boundedPageable = PageRequest.of(pageable.getPageNumber(), clampedSize, pageable.getSort());

        List<MachineryEntity> activeMachines = machineryRepository.findAll()
                .stream()
                .filter(m -> m.getStatus() == OperationalStatus.ACTIVE)
                .filter(m -> {
                    if (!StringUtils.hasText(category)) return true;
                    try {
                        return m.getCategory() == MachineryCategory.valueOf(category.toUpperCase().trim());
                    } catch (IllegalArgumentException e) {
                        return true;
                    }
                })
                .toList();

        List<MachineryDiscoveryResponseDto> discoveryList = activeMachines.stream()
                .map(m -> {
                    Double dist = null;
                    if (latitude != null && longitude != null && m.getLocationData() != null &&
                            m.getLocationData().getLatitude() != null && m.getLocationData().getLongitude() != null) {
                        dist = calculateDistanceKm(
                                latitude,
                                longitude,
                                m.getLocationData().getLatitude(),
                                m.getLocationData().getLongitude()
                        );
                        dist = Math.round(dist * 10.0) / 10.0; // Round to 1 decimal place
                    }

                    // Check if machine is available for requested dates
                    boolean available = true;
                    if (startDate != null && endDate != null) {
                        available = !rentalRequestRepository.existsOverlappingAcceptedBooking(m.getId(), startDate, endDate, null);
                    }

                    return mapToDiscoveryDto(m, dist, available);
                })
                .filter(dto -> {
                    if (latitude != null && longitude != null) {
                        return dto.approximateDistanceKm() != null && dto.approximateDistanceKm() <= searchRadius;
                    }
                    return true;
                })
                .sorted(Comparator.comparing(dto -> dto.approximateDistanceKm() != null ? dto.approximateDistanceKm() : Double.MAX_VALUE))
                .toList();

        int start = (int) boundedPageable.getOffset();
        int end = Math.min((start + boundedPageable.getPageSize()), discoveryList.size());
        List<MachineryDiscoveryResponseDto> pageContent = start <= end ? discoveryList.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, boundedPageable, discoveryList.size());
    }

    private LocationSource parseSource(String sourceStr) {
        try {
            return LocationSource.valueOf(sourceStr.toUpperCase().trim());
        } catch (Exception e) {
            return LocationSource.MANUAL;
        }
    }

    private MachineryResponseDto mapToMachineryDto(MachineryEntity entity) {
        UserEntity owner = entity.getOwnerUser();
        String ownerName = "Machinery Owner";
        var profileOpt = profileRepository.findByUserId(owner.getId());
        if (profileOpt.isPresent()) {
            ownerName = profileOpt.get().getFullName();
        }

        return new MachineryResponseDto(
                entity.getId(),
                owner.getId(),
                ownerName,
                owner.getMobileNumber(),
                entity.getName(),
                entity.getModelNumber(),
                entity.getCategory().name(),
                entity.getHpRating(),
                entity.getLocation(),
                entity.getDailyRate(),
                entity.getHourlyRate(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private MachineryDiscoveryResponseDto mapToDiscoveryDto(MachineryEntity entity, Double distKm, boolean available) {
        UserEntity owner = entity.getOwnerUser();
        String ownerName = "Machinery Owner";
        var profileOpt = profileRepository.findByUserId(owner.getId());
        if (profileOpt.isPresent()) {
            ownerName = profileOpt.get().getFullName();
        }

        String district = entity.getLocationData() != null ? entity.getLocationData().getDistrict() : null;
        String village = entity.getLocationData() != null ? entity.getLocationData().getVillage() : null;

        // PRIVACY GUARANTEE: Exact latitude and longitude are NOT exposed in MachineryDiscoveryResponseDto!
        return new MachineryDiscoveryResponseDto(
                entity.getId(),
                owner.getId(),
                ownerName,
                entity.getName(),
                entity.getModelNumber(),
                entity.getCategory().name(),
                entity.getHpRating(),
                entity.getLocation(),
                district,
                village,
                distKm,
                entity.getDailyRate(),
                entity.getHourlyRate(),
                entity.getStatus().name(),
                available
        );
    }
}
