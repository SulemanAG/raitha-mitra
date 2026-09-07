package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.entity.OtpMetadataEntity;
import com.raithamitra.backend.exception.ValidationException;
import com.raithamitra.backend.repository.OtpMetadataRepository;
import com.raithamitra.backend.service.SmsOtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Development implementation of SmsOtpService.
 * Generates secure random 6-digit OTPs, stores SHA-256 hashed metadata,
 * enforces 30-second resend cooldown and 5-minute expiration window.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class DevelopmentSmsOtpServiceImpl implements SmsOtpService {

    private static final Logger log = LoggerFactory.getLogger(DevelopmentSmsOtpServiceImpl.class);
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 30;
    private static final int MAX_ATTEMPTS = 3;

    private final OtpMetadataRepository otpMetadataRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public DevelopmentSmsOtpServiceImpl(OtpMetadataRepository otpMetadataRepository) {
        this.otpMetadataRepository = otpMetadataRepository;
    }

    @Override
    @Transactional
    public void generateAndSendOtp(String mobileNumber) {
        Instant now = Instant.now();

        // Enforce resend cooldown check
        Optional<OtpMetadataEntity> latestOpt = otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc(mobileNumber);
        if (latestOpt.isPresent()) {
            OtpMetadataEntity latest = latestOpt.get();
            if (!latest.isVerified() && now.isBefore(latest.getResendAvailableTime())) {
                long waitSeconds = ChronoUnit.SECONDS.between(now, latest.getResendAvailableTime());
                throw new ValidationException("Please wait " + waitSeconds + " seconds before requesting another OTP");
            }
        }

        // Generate 6-digit OTP (e.g. 100000 - 999999)
        int otpInt = 100000 + secureRandom.nextInt(900000);
        String otpStr = String.valueOf(otpInt);

        String otpHash = hashOtp(otpStr);
        Instant expiryTime = now.plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES);
        Instant resendAvailableTime = now.plus(RESEND_COOLDOWN_SECONDS, ChronoUnit.SECONDS);

        OtpMetadataEntity otpEntity = new OtpMetadataEntity(mobileNumber, otpHash, expiryTime, resendAvailableTime);
        otpMetadataRepository.save(otpEntity);

        // Dev mode simulation logging
        log.info("[DEV SMS OTP SIMULATOR] Sent OTP {} to mobile {}", otpStr, mobileNumber);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String mobileNumber, String otp) {
        Instant now = Instant.now();

        OtpMetadataEntity metadata = otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc(mobileNumber)
                .orElseThrow(() -> new ValidationException("No OTP request found for this mobile number"));

        if (metadata.isVerified()) {
            throw new ValidationException("OTP has already been verified");
        }

        if (metadata.getAttemptsCount() >= MAX_ATTEMPTS) {
            throw new ValidationException("Maximum OTP verification attempts exceeded. Please request a new OTP.");
        }

        if (now.isAfter(metadata.getExpiryTime())) {
            throw new ValidationException("OTP has expired. Please request a new OTP.");
        }

        metadata.incrementAttempts();
        String submittedHash = hashOtp(otp);

        if (!metadata.getOtpHash().equals(submittedHash)) {
            otpMetadataRepository.save(metadata);
            throw new ValidationException("Invalid OTP entered. Remaining attempts: " + (MAX_ATTEMPTS - metadata.getAttemptsCount()));
        }

        metadata.setVerified(true);
        otpMetadataRepository.save(metadata);
        return true;
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }
}
