package com.raithamitra.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * User Entity representing user domain persistence model in PostgreSQL.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Column(name = "mobile_number", nullable = false, unique = true, length = 15)
    private String mobileNumber;

    @Column(name = "primary_role", nullable = false, length = 30)
    private String primaryRole;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public UserEntity() {
    }

    public UserEntity(String mobileNumber, String primaryRole, boolean active) {
        this.mobileNumber = mobileNumber;
        this.primaryRole = primaryRole;
        this.active = active;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPrimaryRole() {
        return primaryRole;
    }

    public void setPrimaryRole(String primaryRole) {
        this.primaryRole = primaryRole;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static UserEntityBuilder builder() {
        return new UserEntityBuilder();
    }

    public static class UserEntityBuilder {
        private String mobileNumber;
        private String primaryRole;
        private boolean active = true;

        public UserEntityBuilder mobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public UserEntityBuilder primaryRole(String primaryRole) {
            this.primaryRole = primaryRole;
            return this;
        }

        public UserEntityBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public UserEntity build() {
            return new UserEntity(mobileNumber, primaryRole, active);
        }
    }
}
