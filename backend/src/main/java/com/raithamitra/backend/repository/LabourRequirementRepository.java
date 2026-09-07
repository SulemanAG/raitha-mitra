package com.raithamitra.backend.repository;

import com.raithamitra.backend.entity.LabourRequirementEntity;
import com.raithamitra.backend.entity.RequirementStatus;
import com.raithamitra.backend.entity.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository interface for LabourRequirementEntity persistence operations.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Repository
public interface LabourRequirementRepository extends JpaRepository<LabourRequirementEntity, UUID>, JpaSpecificationExecutor<LabourRequirementEntity> {

    Page<LabourRequirementEntity> findByFarmerUserId(UUID farmerUserId, Pageable pageable);

    Page<LabourRequirementEntity> findByStatus(RequirementStatus status, Pageable pageable);

    Page<LabourRequirementEntity> findByStatusAndTaskType(RequirementStatus status, TaskType taskType, Pageable pageable);
}
