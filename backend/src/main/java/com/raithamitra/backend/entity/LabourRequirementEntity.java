package com.raithamitra.backend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * JPA Entity representing a Labour Requirement posting created by a Farmer.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "labour_requirements")
public class LabourRequirementEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_user_id", nullable = false)
    private UserEntity farmerUser;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 50)
    private TaskType taskType;

    @Column(name = "location", nullable = false, length = 150)
    private String location;

    @Column(name = "required_workers_count", nullable = false)
    private int requiredWorkersCount = 1;

    @Column(name = "daily_offered_wage", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyOfferedWage;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RequirementStatus status = RequirementStatus.OPEN;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "labour_requirement_skills", joinColumns = @JoinColumn(name = "labour_requirement_id"))
    @Column(name = "skill", nullable = false, length = 50)
    private Set<String> requiredSkills = new HashSet<>();

    public LabourRequirementEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabourRequirementEntity that = (LabourRequirementEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
