package com.raithamitra.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * JPA Entity representing Machinery Owner Profile persistence model.
 * Linked 1-to-1 with central UserEntity identity.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "machinery_owner_profiles")
public class MachineryOwnerProfileEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;

    public MachineryOwnerProfileEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MachineryOwnerProfileEntity that = (MachineryOwnerProfileEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
