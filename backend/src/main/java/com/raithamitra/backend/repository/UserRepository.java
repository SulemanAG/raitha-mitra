package com.raithamitra.backend.repository;

import com.raithamitra.backend.entity.AccountStatus;
import com.raithamitra.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for UserEntity persistence operations.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByMobileNumber(String mobileNumber);

    Optional<UserEntity> findByMobileNumberAndAccountStatus(String mobileNumber, AccountStatus accountStatus);

    boolean existsByMobileNumber(String mobileNumber);
}
