package com.raithamitra.backend.entity;

import jakarta.persistence.Column;
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
import java.util.Objects;

/**
 * JPA Entity representing a machinery rental request booking.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Getter
@Setter
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
