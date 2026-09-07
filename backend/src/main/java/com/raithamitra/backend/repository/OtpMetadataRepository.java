package com.raithamitra.backend.repository;

import com.raithamitra.backend.entity.OtpMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for OtpMetadataEntity persistence operations.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Repository
public interface OtpMetadataRepository extends JpaRepository<OtpMetadataEntity, UUID> {

    Optional<OtpMetadataEntity> findTopByMobileNumberOrderByCreatedAtDesc(String mobileNumber);
}
