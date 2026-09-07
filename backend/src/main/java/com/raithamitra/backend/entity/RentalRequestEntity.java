package com.raithamitra.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * JPA Entity representing a machinery rental request booking.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Entity
@Table(name = "machinery_rental_requests")
public class RentalRequestEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machinery_id", nullable = false)
    private MachineryEntity machinery;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "renter_user_id", nullable = false)
    private UserEntity renterUser;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_unit", nullable = false, length = 20)
    private RentalUnit rentalUnit = RentalUnit.DAILY;

    @Column(name = "estimated_units", nullable = false)
    private int estimatedUnits = 1;

    @Column(name = "rate_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerUnit;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RentalStatus status = RentalStatus.PENDING;

    @Column(name = "renter_notes", columnDefinition = "TEXT")
    private String renterNotes;

    @Column(name = "owner_notes", columnDefinition = "TEXT")
    private String ownerNotes;

    public RentalRequestEntity() {
    }

    public MachineryEntity getMachinery() {
        return machinery;
    }

    public void setMachinery(MachineryEntity machinery) {
        this.machinery = machinery;
    }

    public UserEntity getRenterUser() {
        return renterUser;
    }

    public void setRenterUser(UserEntity renterUser) {
        this.renterUser = renterUser;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public RentalUnit getRentalUnit() {
        return rentalUnit;
    }

    public void setRentalUnit(RentalUnit rentalUnit) {
        this.rentalUnit = rentalUnit;
    }

    public int getEstimatedUnits() {
        return estimatedUnits;
    }

    public void setEstimatedUnits(int estimatedUnits) {
        this.estimatedUnits = estimatedUnits;
    }

    public BigDecimal getRatePerUnit() {
        return ratePerUnit;
    }

    public void setRatePerUnit(BigDecimal ratePerUnit) {
        this.ratePerUnit = ratePerUnit;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public String getRenterNotes() {
        return renterNotes;
    }

    public void setRenterNotes(String renterNotes) {
        this.renterNotes = renterNotes;
    }

    public String getOwnerNotes() {
        return ownerNotes;
    }

    public void setOwnerNotes(String ownerNotes) {
        this.ownerNotes = ownerNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentalRequestEntity that = (RentalRequestEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
