package com.raithamitra.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * User Entity representing user domain persistence model in PostgreSQL.
 * Serves as central account identity with multi-role mapping and composite profile relationships.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Column(name = "mobile_number", nullable = false, unique = true, length = 15)
    private String mobileNumber;

    @Column(name = "primary_role", nullable = false, length = 30)
    private String primaryRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 30)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FarmerProfileEntity farmerProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LabourerProfileEntity labourerProfile;

    public UserEntity() {
    }

    public UserEntity(String mobileNumber, String primaryRole, AccountStatus accountStatus, Set<UserRole> roles) {
        this.mobileNumber = mobileNumber;
        this.primaryRole = primaryRole;
        this.accountStatus = accountStatus != null ? accountStatus : AccountStatus.ACTIVE;
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public boolean isActive() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }

    public void setFarmerProfile(FarmerProfileEntity farmerProfile) {
        this.farmerProfile = farmerProfile;
        if (farmerProfile != null) {
            farmerProfile.setUser(this);
        }
    }

    public void setLabourerProfile(LabourerProfileEntity labourerProfile) {
        this.labourerProfile = labourerProfile;
        if (labourerProfile != null) {
            labourerProfile.setUser(this);
        }
    }

    public static UserEntityBuilder builder() {
        return new UserEntityBuilder();
    }

    public static class UserEntityBuilder {
        private String mobileNumber;
        private String primaryRole;
        private AccountStatus accountStatus = AccountStatus.ACTIVE;
        private Set<UserRole> roles = new HashSet<>();

        public UserEntityBuilder mobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public UserEntityBuilder primaryRole(String primaryRole) {
            this.primaryRole = primaryRole;
            return this;
        }

        public UserEntityBuilder accountStatus(AccountStatus accountStatus) {
            this.accountStatus = accountStatus;
            return this;
        }

        public UserEntityBuilder roles(Set<UserRole> roles) {
            this.roles = roles;
            return this;
        }

        public UserEntityBuilder active(boolean active) {
            this.accountStatus = active ? AccountStatus.ACTIVE : AccountStatus.DEACTIVATED;
            return this;
        }

        public UserEntity build() {
            UserEntity entity = new UserEntity(mobileNumber, primaryRole, accountStatus, roles);
            return entity;
        }
    }
}
