package com.raithamitra.backend.repository;

import com.raithamitra.backend.entity.MachineryOwnerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for MachineryOwnerProfileEntity persistence operations.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Repository
public interface MachineryOwnerProfileRepository extends JpaRepository<MachineryOwnerProfileEntity, UUID> {

    Optional<MachineryOwnerProfileEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
