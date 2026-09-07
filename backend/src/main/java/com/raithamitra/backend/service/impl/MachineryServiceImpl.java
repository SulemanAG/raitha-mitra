package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.CreateMachineryRequestDto;
import com.raithamitra.backend.dto.request.UpdateMachineryRequestDto;
import com.raithamitra.backend.dto.response.MachineryResponseDto;
import com.raithamitra.backend.entity.MachineryCategory;
import com.raithamitra.backend.entity.MachineryEntity;
import com.raithamitra.backend.entity.OperationalStatus;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.MachineryOwnerProfileRepository;
import com.raithamitra.backend.repository.MachineryRepository;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.SecurityUtils;
import com.raithamitra.backend.service.MachineryService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation managing agricultural machinery registration, updates, operational state changes,
 * server-side owner verification, and paginated discovery filtering.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class MachineryServiceImpl implements MachineryService {

    private static final Logger log = LoggerFactory.getLogger(MachineryServiceImpl.class);

    private final MachineryRepository machineryRepository;
    private final UserRepository userRepository;
    private final MachineryOwnerProfileRepository profileRepository;

    public MachineryServiceImpl(MachineryRepository machineryRepository, UserRepository userRepository, MachineryOwnerProfileRepository profileRepository) {
        this.machineryRepository = machineryRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public MachineryResponseDto registerMachinery(CreateMachineryRequestDto requestDto) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UserEntity ownerUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        MachineryCategory categoryEnum = parseCategory(requestDto.category());

        MachineryEntity entity = new MachineryEntity();
        entity.setOwnerUser(ownerUser);
        entity.setName(requestDto.name());
        entity.setModelNumber(requestDto.modelNumber());
        entity.setCategory(categoryEnum);
        entity.setHpRating(requestDto.hpRating());
        entity.setLocation(requestDto.location());
        entity.setDailyRate(requestDto.dailyRate());
        entity.setHourlyRate(requestDto.hourlyRate());
        entity.setStatus(OperationalStatus.ACTIVE);

        MachineryEntity saved = machineryRepository.save(entity);
        log.info("Registered machinery ID: {} by owner ID: {}", saved.getId(), currentUserId);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MachineryResponseDto getMachineryById(UUID id) {
        MachineryEntity entity = machineryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machinery", "id", id));
        return mapToDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineryResponseDto> getMyMachinery(Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return machineryRepository.findByOwnerUserId(currentUserId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional
    public MachineryResponseDto updateMachinery(UUID id, UpdateMachineryRequestDto requestDto) {
        MachineryEntity entity = machineryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machinery", "id", id));

        verifyOwnership(entity);

        entity.setName(requestDto.name());
        entity.setModelNumber(requestDto.modelNumber());
        entity.setCategory(parseCategory(requestDto.category()));
        entity.setHpRating(requestDto.hpRating());
        entity.setLocation(requestDto.location());
        entity.setDailyRate(requestDto.dailyRate());
        entity.setHourlyRate(requestDto.hourlyRate());
        entity.setStatus(parseStatus(requestDto.status()));

        MachineryEntity updated = machineryRepository.save(entity);
        log.info("Updated machinery ID: {}", updated.getId());
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public MachineryResponseDto toggleMachineryStatus(UUID id, String statusStr) {
        MachineryEntity entity = machineryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machinery", "id", id));

        verifyOwnership(entity);

        OperationalStatus newStatus = parseStatus(statusStr);
        entity.setStatus(newStatus);

        MachineryEntity updated = machineryRepository.save(entity);
        log.info("Toggled operational status for machinery ID: {} to {}", updated.getId(), newStatus);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteMachinery(UUID id) {
        MachineryEntity entity = machineryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machinery", "id", id));

        verifyOwnership(entity);

        machineryRepository.delete(entity);
        log.info("Deleted machinery ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineryResponseDto> searchMachinery(String location, String category, String status, Pageable pageable) {
        Specification<MachineryEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(location)) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase().trim() + "%"));
            }

            if (StringUtils.hasText(category)) {
                try {
                    MachineryCategory mc = MachineryCategory.valueOf(category.toUpperCase().trim());
                    predicates.add(cb.equal(root.get("category"), mc));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (StringUtils.hasText(status)) {
                try {
                    OperationalStatus opStatus = OperationalStatus.valueOf(status.toUpperCase().trim());
                    predicates.add(cb.equal(root.get("status"), opStatus));
                } catch (IllegalArgumentException ignored) {
                }
            } else {
                predicates.add(cb.equal(root.get("status"), OperationalStatus.ACTIVE));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return machineryRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    private void verifyOwnership(MachineryEntity entity) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        if (!entity.getOwnerUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("User is not authorized to modify this machinery asset");
        }
    }

    private MachineryResponseDto mapToDto(MachineryEntity entity) {
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

    private MachineryCategory parseCategory(String categoryStr) {
        try {
            return MachineryCategory.valueOf(categoryStr.toUpperCase().trim());
        } catch (Exception e) {
            return MachineryCategory.OTHER;
        }
    }

    private OperationalStatus parseStatus(String statusStr) {
        try {
            return OperationalStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (Exception e) {
            return OperationalStatus.ACTIVE;
        }
    }
}
