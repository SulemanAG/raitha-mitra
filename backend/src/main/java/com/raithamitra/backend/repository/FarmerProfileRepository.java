package com.raithamitra.backend.repository;

import com.raithamitra.backend.entity.FarmerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for FarmerProfileEntity persistence operations.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfileEntity, UUID> {

    Optional<FarmerProfileEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
