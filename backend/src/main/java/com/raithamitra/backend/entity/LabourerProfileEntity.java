package com.raithamitra.backend.entity;

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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Labourer Profile Entity representing agricultural worker-specific domain details.
 * Composes 1-to-1 with UserEntity and contains normalized skill collection.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "labourer_profiles")
public class LabourerProfileEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "daily_wage_rate", precision = 10, scale = 2)
    private BigDecimal dailyWageRate;

    @Column(name = "experience_years")
    private Integer experienceYears = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 30)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    @ElementCollection(targetClass = LabourSkill.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "labourer_skills", joinColumns = @JoinColumn(name = "labourer_profile_id"))
    @Column(name = "skill", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Set<LabourSkill> skills = new HashSet<>();

    public LabourerProfileEntity() {
    }

    public LabourerProfileEntity(UserEntity user, String fullName, BigDecimal dailyWageRate, Integer experienceYears, AvailabilityStatus availabilityStatus, Set<LabourSkill> skills) {
        this.user = user;
        this.fullName = fullName;
        this.dailyWageRate = dailyWageRate;
        this.experienceYears = experienceYears != null ? experienceYears : 0;
        this.availabilityStatus = availabilityStatus != null ? availabilityStatus : AvailabilityStatus.AVAILABLE;
        this.skills = skills != null ? skills : new HashSet<>();
    }
}
