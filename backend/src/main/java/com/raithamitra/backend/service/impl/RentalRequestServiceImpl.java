package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.CreateRentalRequestDto;
import com.raithamitra.backend.dto.request.UpdateRentalStatusRequestDto;
import com.raithamitra.backend.dto.response.RentalRequestResponseDto;
import com.raithamitra.backend.entity.MachineryEntity;
import com.raithamitra.backend.entity.OperationalStatus;
import com.raithamitra.backend.entity.RentalRequestEntity;
import com.raithamitra.backend.entity.RentalStatus;
import com.raithamitra.backend.entity.RentalUnit;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.exception.InvalidStateTransitionException;
import com.raithamitra.backend.exception.RentalPeriodConflictException;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.exception.ValidationException;
import com.raithamitra.backend.repository.MachineryRepository;
import com.raithamitra.backend.repository.RentalRequestRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.SecurityUtils;
import com.raithamitra.backend.service.RentalRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation executing machinery rental booking requests, authoritative amount calculations,
 * pessimistic locking on approval, and automatic conflict resolution for overlapping pending requests.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class RentalRequestServiceImpl implements RentalRequestService {

    private static final Logger log = LoggerFactory.getLogger(RentalRequestServiceImpl.class);

    private final RentalRequestRepository rentalRequestRepository;
    private final MachineryRepository machineryRepository;
    private final UserRepository userRepository;

    public RentalRequestServiceImpl(
            RentalRequestRepository rentalRequestRepository,
            MachineryRepository machineryRepository,
            UserRepository userRepository
    ) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.machineryRepository = machineryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public RentalRequestResponseDto createRentalRequest(CreateRentalRequestDto requestDto) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UserEntity renterUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        if (requestDto.endDate().isBefore(requestDto.startDate())) {
            throw new ValidationException("Rental end date cannot be before start date");
        }

        MachineryEntity machinery = machineryRepository.findById(requestDto.machineryId())
                .orElseThrow(() -> new ResourceNotFoundException("Machinery", "id", requestDto.machineryId()));

        if (machinery.getStatus() != OperationalStatus.ACTIVE) {
            throw new ValidationException("Machinery is currently unavailable for rental");
        }

        if (machinery.getOwnerUser().getId().equals(currentUserId)) {
            throw new ValidationException("Machinery owner cannot rent their own machine");
        }

        // Check if there's already an accepted booking overlapping the requested dates
        boolean conflictExists = rentalRequestRepository.existsOverlappingAcceptedBooking(
                machinery.getId(),
                requestDto.startDate(),
                requestDto.endDate(),
                null
        );
        if (conflictExists) {
            throw new RentalPeriodConflictException("Selected dates overlap with an existing confirmed booking for this machine");
        }

        RentalUnit unitEnum = parseRentalUnit(requestDto.rentalUnit());
        BigDecimal rate = unitEnum == RentalUnit.HOURLY && machinery.getHourlyRate() != null
                ? machinery.getHourlyRate()
                : machinery.getDailyRate();

        int units = requestDto.estimatedUnits() > 0 ? requestDto.estimatedUnits() : 1;
        BigDecimal totalAmount = rate.multiply(BigDecimal.valueOf(units));

        RentalRequestEntity entity = new RentalRequestEntity();
        entity.setMachinery(machinery);
        entity.setRenterUser(renterUser);
        entity.setStartDate(requestDto.startDate());
        entity.setEndDate(requestDto.endDate());
        entity.setRentalUnit(unitEnum);
        entity.setEstimatedUnits(units);
        entity.setRatePerUnit(rate);
        entity.setTotalAmount(totalAmount);
        entity.setStatus(RentalStatus.PENDING);
        entity.setRenterNotes(requestDto.renterNotes());

        RentalRequestEntity saved = rentalRequestRepository.save(entity);
        log.info("Created rental request ID: {} for machinery ID: {} by renter ID: {}", saved.getId(), machinery.getId(), currentUserId);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalRequestResponseDto getRentalRequestById(UUID id) {
        RentalRequestEntity entity = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalRequest", "id", id));
        return mapToDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalRequestResponseDto> getMyRentalRequests(Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return rentalRequestRepository.findByRenterUserId(currentUserId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalRequestResponseDto> getOwnerRentalRequests(Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return rentalRequestRepository.findByMachineryOwnerUserId(currentUserId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional
    public RentalRequestResponseDto acceptRentalRequest(UUID id, UpdateRentalStatusRequestDto requestDto) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        // Fetch using pessimistic write lock to serialize concurrent approval attempts
        RentalRequestEntity entity = rentalRequestRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalRequest", "id", id));

        if (!entity.getMachinery().getOwnerUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User is not authorized to respond to this rental request");
        }

        if (entity.getStatus() != RentalStatus.PENDING) {
            throw new InvalidStateTransitionException("Only PENDING rental requests can be accepted");
        }

        // Concurrency Invariant Check: Verify no other accepted booking overlaps the date range
        boolean conflict = rentalRequestRepository.existsOverlappingAcceptedBooking(
                entity.getMachinery().getId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getId()
        );
        if (conflict) {
            throw new RentalPeriodConflictException("Cannot accept request: an overlapping booking has already been accepted for these dates");
        }

        entity.setStatus(RentalStatus.ACCEPTED);
        if (requestDto != null && StringUtils.hasText(requestDto.ownerNotes())) {
            entity.setOwnerNotes(requestDto.ownerNotes());
        }

        RentalRequestEntity accepted = rentalRequestRepository.save(entity);
        log.info("Accepted rental request ID: {} for machinery ID: {}", accepted.getId(), entity.getMachinery().getId());

        // Automatically reject all other PENDING requests overlapping this newly accepted period
        List<RentalRequestEntity> overlappingPending = rentalRequestRepository.findOverlappingPendingRequests(
                entity.getMachinery().getId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getId()
        );
        for (RentalRequestEntity pendingReq : overlappingPending) {
            pendingReq.setStatus(RentalStatus.REJECTED);
            pendingReq.setOwnerNotes("Automatically rejected due to conflicting accepted booking for " + entity.getStartDate() + " to " + entity.getEndDate());
            rentalRequestRepository.save(pendingReq);
            log.info("Auto-rejected conflicting pending rental request ID: {}", pendingReq.getId());
        }

        return mapToDto(accepted);
    }

    @Override
    @Transactional
    public RentalRequestResponseDto rejectRentalRequest(UUID id, UpdateRentalStatusRequestDto requestDto) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        RentalRequestEntity entity = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalRequest", "id", id));

        if (!entity.getMachinery().getOwnerUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User is not authorized to respond to this rental request");
        }

        if (entity.getStatus() != RentalStatus.PENDING) {
            throw new InvalidStateTransitionException("Only PENDING rental requests can be rejected");
        }

        entity.setStatus(RentalStatus.REJECTED);
        if (requestDto != null && StringUtils.hasText(requestDto.ownerNotes())) {
            entity.setOwnerNotes(requestDto.ownerNotes());
        }

        RentalRequestEntity updated = rentalRequestRepository.save(entity);
        log.info("Rejected rental request ID: {}", updated.getId());
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public RentalRequestResponseDto cancelRentalRequest(UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        RentalRequestEntity entity = rentalRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalRequest", "id", id));

        boolean isRenter = entity.getRenterUser().getId().equals(currentUserId);
        boolean isOwner = entity.getMachinery().getOwnerUser().getId().equals(currentUserId);

        if (!isRenter && !isOwner) {
            throw new AccessDeniedException("User is not authorized to cancel this rental request");
        }

        if (entity.getStatus() == RentalStatus.COMPLETED || entity.getStatus() == RentalStatus.CANCELLED || entity.getStatus() == RentalStatus.REJECTED) {
            throw new InvalidStateTransitionException("Cannot cancel a request that is already " + entity.getStatus());
        }

        entity.setStatus(RentalStatus.CANCELLED);
        RentalRequestEntity updated = rentalRequestRepository.save(entity);
        log.info("Cancelled rental request ID: {}", updated.getId());
        return mapToDto(updated);
    }

    private RentalRequestResponseDto mapToDto(RentalRequestEntity entity) {
        MachineryEntity machine = entity.getMachinery();
        UserEntity owner = machine.getOwnerUser();
        UserEntity renter = entity.getRenterUser();

        return new RentalRequestResponseDto(
                entity.getId(),
                machine.getId(),
                machine.getName(),
                machine.getCategory().name(),
                owner.getId(),
                owner.getMobileNumber(),
                owner.getMobileNumber(),
                renter.getId(),
                renter.getMobileNumber(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getRentalUnit().name(),
                entity.getEstimatedUnits(),
                entity.getRatePerUnit(),
                entity.getTotalAmount(),
                entity.getStatus().name(),
                entity.getRenterNotes(),
                entity.getOwnerNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private RentalUnit parseRentalUnit(String unitStr) {
        try {
            return RentalUnit.valueOf(unitStr.toUpperCase().trim());
        } catch (Exception e) {
            return RentalUnit.DAILY;
        }
    }
}
