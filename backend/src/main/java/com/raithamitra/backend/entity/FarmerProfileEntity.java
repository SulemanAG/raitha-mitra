package com.raithamitra.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Farmer Profile Entity representing farmer-specific domain details.
 * Composes 1-to-1 with UserEntity without duplicating identity credentials.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Entity
@Table(name = "farmer_profiles")
public class FarmerProfileEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "farm_location", length = 150)
    private String farmLocation;

    @Column(name = "farm_size_acres", precision = 8, scale = 2)
    private BigDecimal farmSizeAcres;

    @Column(name = "preferred_crop_types", length = 255)
    private String preferredCropTypes;

    public FarmerProfileEntity() {
    }

    public FarmerProfileEntity(UserEntity user, String fullName, String farmLocation, BigDecimal farmSizeAcres, String preferredCropTypes) {
        this.user = user;
        this.fullName = fullName;
        this.farmLocation = farmLocation;
        this.farmSizeAcres = farmSizeAcres;
        this.preferredCropTypes = preferredCropTypes;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFarmLocation() {
        return farmLocation;
    }

    public void setFarmLocation(String farmLocation) {
        this.farmLocation = farmLocation;
    }

    public BigDecimal getFarmSizeAcres() {
        return farmSizeAcres;
    }

    public void setFarmSizeAcres(BigDecimal farmSizeAcres) {
        this.farmSizeAcres = farmSizeAcres;
    }

    public String getPreferredCropTypes() {
        return preferredCropTypes;
    }

    public void setPreferredCropTypes(String preferredCropTypes) {
        this.preferredCropTypes = preferredCropTypes;
    }
}
