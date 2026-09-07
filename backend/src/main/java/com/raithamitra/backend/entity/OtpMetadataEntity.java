package com.raithamitra.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity representing the OTP Lifecycle metadata.
 * Stores cryptographically hashed OTPs, expiration timestamps, attempt limits, and verification status.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Entity
@Table(name = "otp_metadata")
public class OtpMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    @Column(name = "otp_hash", nullable = false, length = 100)
    private String otpHash;

    @Column(name = "expiry_time", nullable = false)
    private Instant expiryTime;

    @Column(name = "resend_available_time", nullable = false)
    private Instant resendAvailableTime;

    @Column(name = "attempts_count", nullable = false)
    private int attemptsCount = 0;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public OtpMetadataEntity() {
    }

    public OtpMetadataEntity(String mobileNumber, String otpHash, Instant expiryTime, Instant resendAvailableTime) {
        this.mobileNumber = mobileNumber;
        this.otpHash = otpHash;
        this.expiryTime = expiryTime;
        this.resendAvailableTime = resendAvailableTime;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Instant expiryTime) {
        this.expiryTime = expiryTime;
    }

    public Instant getResendAvailableTime() {
        return resendAvailableTime;
    }

    public void setResendAvailableTime(Instant resendAvailableTime) {
        this.resendAvailableTime = resendAvailableTime;
    }

    public int getAttemptsCount() {
        return attemptsCount;
    }

    public void setAttemptsCount(int attemptsCount) {
        this.attemptsCount = attemptsCount;
    }

    public void incrementAttempts() {
        this.attemptsCount++;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtpMetadataEntity that = (OtpMetadataEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
