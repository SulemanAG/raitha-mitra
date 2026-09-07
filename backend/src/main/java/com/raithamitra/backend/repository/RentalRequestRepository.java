package com.raithamitra.backend.repository;

import com.raithamitra.backend.entity.RentalRequestEntity;
import com.raithamitra.backend.entity.RentalStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for RentalRequestEntity persistence operations.
 * Includes pessimistic locking and overlap checking query methods to prevent concurrent double-bookings.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Repository
public interface RentalRequestRepository extends JpaRepository<RentalRequestEntity, UUID> {

    Page<RentalRequestEntity> findByRenterUserId(UUID renterUserId, Pageable pageable);

    @Query("SELECT r FROM RentalRequestEntity r WHERE r.machinery.ownerUser.id = :ownerUserId")
    Page<RentalRequestEntity> findByMachineryOwnerUserId(@Param("ownerUserId") UUID ownerUserId, Pageable pageable);

    Page<RentalRequestEntity> findByMachineryId(UUID machineryId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RentalRequestEntity r WHERE r.id = :id")
    Optional<RentalRequestEntity> findByIdForUpdate(@Param("id") UUID id);

    @Query("SELECT COUNT(r) > 0 FROM RentalRequestEntity r WHERE r.machinery.id = :machineryId AND r.status = 'ACCEPTED' AND (r.startDate <= :endDate AND r.endDate >= :startDate) AND (:excludeRequestId IS NULL OR r.id <> :excludeRequestId)")
    boolean existsOverlappingAcceptedBooking(
            @Param("machineryId") UUID machineryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeRequestId") UUID excludeRequestId
    );

    @Query("SELECT r FROM RentalRequestEntity r WHERE r.machinery.id = :machineryId AND r.status = 'PENDING' AND r.id <> :acceptedRequestId AND (r.startDate <= :endDate AND r.endDate >= :startDate)")
    List<RentalRequestEntity> findOverlappingPendingRequests(
            @Param("machineryId") UUID machineryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("acceptedRequestId") UUID acceptedRequestId
    );
}
