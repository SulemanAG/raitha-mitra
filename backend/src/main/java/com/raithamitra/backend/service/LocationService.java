package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.UpdateLocationRequestDto;
import com.raithamitra.backend.dto.response.MachineryDiscoveryResponseDto;
import com.raithamitra.backend.dto.response.MachineryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service interface managing spatial distance calculations, coordinate validation,
 * privacy-safe geospatial discovery filtering, and optimistic concurrency locking on location updates.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface LocationService {

    void validateCoordinates(Double latitude, Double longitude);

    double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2);

    MachineryResponseDto updateMachineryLocation(UUID machineryId, UpdateLocationRequestDto requestDto);

    Page<MachineryDiscoveryResponseDto> discoverNearbyMachinery(
            Double latitude,
            Double longitude,
            Double radiusKm,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
