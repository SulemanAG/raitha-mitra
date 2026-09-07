package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.entity.OtpMetadataEntity;
import com.raithamitra.backend.exception.ValidationException;
import com.raithamitra.backend.repository.OtpMetadataRepository;
import com.raithamitra.backend.service.SmsOtpService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Production implementation of SmsOtpService communicating with MSG91 HTTPS REST API.
 * Active exclusively in the production profile ('prod').
 *
 * <p>Spring Boot maintains local cryptographic authority (SHA-256 OTP hashing, expiry, attempt caps),
 * while MSG91 handles secure external SMS delivery over HTTPS.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
@Profile("prod")
public class Msg91SmsOtpServiceImpl implements SmsOtpService {

    private static final Logger log = LoggerFactory.getLogger(Msg91SmsOtpServiceImpl.class);
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 30;
    private static final int MAX_ATTEMPTS = 3;
    private static final String DEFAULT_MSG91_URL = "https://control.msg91.com/api/v5/flow/";

    private final OtpMetadataRepository otpMetadataRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final RestClient restClient;

    @Value("${app.msg91.auth-key:}")
    private String authKey;

    @Value("${app.msg91.template-id:}")
    private String templateId;

    @Value("${app.msg91.sender-id:RAITHA}")
    private String senderId;

    @Value("${app.msg91.api-url:https://control.msg91.com/api/v5/flow/}")
    private String apiUrl;

    public Msg91SmsOtpServiceImpl(OtpMetadataRepository otpMetadataRepository) {
        this(otpMetadataRepository, createDefaultRestClient());
    }

    public Msg91SmsOtpServiceImpl(OtpMetadataRepository otpMetadataRepository, RestClient restClient) {
        this.otpMetadataRepository = otpMetadataRepository;
        this.restClient = restClient;
    }

    private static RestClient createDefaultRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);
        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    @PostConstruct
    public void validateProductionConfiguration() {
        if (!StringUtils.hasText(authKey) || !StringUtils.hasText(templateId)) {
            throw new IllegalStateException(
                    "Production profile 'prod' requires 'app.msg91.auth-key' and 'app.msg91.template-id' environment variables. " +
                    "Application startup aborted to prevent unauthenticated or mock OTP execution."
            );
        }
        log.info("Successfully initialized MSG91 production SMS OTP provider with template ID: {}", templateId);
    }

    @Override
    @Transactional
    public void generateAndSendOtp(String mobileNumber) {
        String normalizedMobile = normalizePhoneNumber(mobileNumber);
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

        // Generate 6-digit OTP
        int otpInt = 100000 + secureRandom.nextInt(900000);
        String otpStr = String.valueOf(otpInt);

        String otpHash = hashOtp(otpStr);
        Instant expiryTime = now.plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES);
        Instant resendAvailableTime = now.plus(RESEND_COOLDOWN_SECONDS, ChronoUnit.SECONDS);

        // Send SMS via MSG91 HTTPS REST API
        dispatchSmsViaMsg91(normalizedMobile, otpStr);

        // Persist local OTP lifecycle metadata
        OtpMetadataEntity otpEntity = new OtpMetadataEntity(mobileNumber, otpHash, expiryTime, resendAvailableTime);
        otpMetadataRepository.save(otpEntity);
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

    private void dispatchSmsViaMsg91(String normalizedMobile, String otp) {
        Map<String, Object> recipient = Map.of(
                "mobiles", normalizedMobile,
                "otp", otp
        );

        Map<String, Object> requestBody = Map.of(
                "template_id", templateId,
                "short_url", "0",
                "recipients", List.of(recipient)
        );

        try {
            restClient.post()
                    .uri(apiUrl)
                    .header("authkey", authKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                        int statusCode = resp.getStatusCode().value();
                        if (statusCode == 401 || statusCode == 403) {
                            log.error("MSG91 authentication failure (HTTP {}). Verify MSG91_AUTH_KEY.", statusCode);
                            throw new ValidationException("OTP delivery failed due to invalid provider authentication configuration.");
                        } else if (statusCode == 429) {
                            log.warn("MSG91 rate limit exceeded (HTTP 429).");
                            throw new ValidationException("SMS delivery rate limit exceeded. Please try again later.");
                        }
                        throw new ValidationException("OTP delivery rejected by provider (HTTP " + statusCode + ").");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, resp) -> {
                        log.error("MSG91 server error (HTTP {}).", resp.getStatusCode().value());
                        throw new ValidationException("OTP SMS service is currently unavailable. Please try again later.");
                    })
                    .toBodilessEntity();

            log.info("Successfully dispatched SMS OTP via MSG91 to mobile ending in {}", maskMobile(normalizedMobile));
        } catch (ValidationException ve) {
            throw ve;
        } catch (RestClientException rce) {
            log.error("Network or timeout error while contacting MSG91 API: {}", rce.getMessage());
            throw new ValidationException("OTP SMS service is currently unavailable due to network timeout. Please try again.");
        } catch (Exception e) {
            log.error("Unexpected failure during MSG91 SMS dispatch", e);
            throw new ValidationException("Failed to send OTP SMS. Please try again.");
        }
    }

    public String normalizePhoneNumber(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            throw new ValidationException("Mobile number is required");
        }
        String clean = mobile.replaceAll("[^0-9]", "");
        if (clean.length() == 10 && clean.matches("^[6-9]\\d{9}$")) {
            return "91" + clean;
        } else if (clean.length() == 12 && clean.startsWith("91") && clean.substring(2).matches("^[6-9]\\d{9}$")) {
            return clean;
        }
        throw new ValidationException("Invalid Indian mobile number format. Expected 10-digit mobile number.");
    }

    private String maskMobile(String mobile) {
        if (mobile != null && mobile.length() >= 4) {
            return "*****" + mobile.substring(mobile.length() - 4);
        }
        return "*****";
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
