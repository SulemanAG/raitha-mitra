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

    public UserEntity getFarmerUser() {
        return farmerUser;
    }

    public void setFarmerUser(UserEntity farmerUser) {
        this.farmerUser = farmerUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRequiredWorkersCount() {
        return requiredWorkersCount;
    }

    public void setRequiredWorkersCount(int requiredWorkersCount) {
        this.requiredWorkersCount = requiredWorkersCount;
    }

    public BigDecimal getDailyOfferedWage() {
        return dailyOfferedWage;
    }

    public void setDailyOfferedWage(BigDecimal dailyOfferedWage) {
        this.dailyOfferedWage = dailyOfferedWage;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public RequirementStatus getStatus() {
        return status;
    }

    public void setStatus(RequirementStatus status) {
        this.status = status;
    }

    public Set<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<String> requiredSkills) {
        this.requiredSkills = requiredSkills != null ? requiredSkills : new HashSet<>();
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
