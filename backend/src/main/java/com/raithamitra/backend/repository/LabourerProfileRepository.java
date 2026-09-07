package com.raithamitra.backend.repository;

import com.raithamitra.backend.entity.AvailabilityStatus;
import com.raithamitra.backend.entity.LabourSkill;
import com.raithamitra.backend.entity.LabourerProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for LabourerProfileEntity persistence & paginated skill discovery.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Repository
public interface LabourerProfileRepository extends JpaRepository<LabourerProfileEntity, UUID> {

    Optional<LabourerProfileEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    Page<LabourerProfileEntity> findByAvailabilityStatus(AvailabilityStatus availabilityStatus, Pageable pageable);

    @Query("SELECT DISTINCT l FROM LabourerProfileEntity l JOIN l.skills s WHERE l.availabilityStatus = :status AND s = :skill")
    Page<LabourerProfileEntity> findByAvailabilityAndSkill(
            @Param("status") AvailabilityStatus status,
            @Param("skill") LabourSkill skill,
            Pageable pageable
    );
}
