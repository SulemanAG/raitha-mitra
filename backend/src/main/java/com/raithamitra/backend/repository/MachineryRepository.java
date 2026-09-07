package com.raithamitra.backend.repository;

import com.raithamitra.backend.entity.MachineryCategory;
import com.raithamitra.backend.entity.MachineryEntity;
import com.raithamitra.backend.entity.OperationalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository interface for MachineryEntity persistence operations.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Repository
public interface MachineryRepository extends JpaRepository<MachineryEntity, UUID>, JpaSpecificationExecutor<MachineryEntity> {

    Page<MachineryEntity> findByOwnerUserId(UUID ownerUserId, Pageable pageable);

    Page<MachineryEntity> findByStatus(OperationalStatus status, Pageable pageable);

    Page<MachineryEntity> findByStatusAndCategory(OperationalStatus status, MachineryCategory category, Pageable pageable);
}
