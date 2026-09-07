package com.raithamitra.backend.service;

import com.raithamitra.backend.entity.OtpMetadataEntity;
import com.raithamitra.backend.exception.ValidationException;
import com.raithamitra.backend.repository.OtpMetadataRepository;
import com.raithamitra.backend.service.impl.DevelopmentSmsOtpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Test verifying DevelopmentSmsOtpServiceImpl logic using Mockito.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class SmsOtpServiceTest {

    @Mock
    private OtpMetadataRepository otpMetadataRepository;

    @InjectMocks
    private DevelopmentSmsOtpServiceImpl smsOtpService;

    private String sampleMobile;

    @BeforeEach
    void setUp() {
        sampleMobile = "+919876543210";
    }

    @Test
    void generateAndSendOtp_ShouldSaveOtpMetadata_WhenNoPreviousRequestExists() {
        when(otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc(sampleMobile)).thenReturn(Optional.empty());

        smsOtpService.generateAndSendOtp(sampleMobile);

        verify(otpMetadataRepository).save(any(OtpMetadataEntity.class));
    }

    @Test
    void generateAndSendOtp_ShouldThrowValidationException_WhenResendCooldownActive() {
        Instant now = Instant.now();
        OtpMetadataEntity activeMetadata = new OtpMetadataEntity(
                sampleMobile,
                "dummyHash",
                now.plus(5, ChronoUnit.MINUTES),
                now.plus(25, ChronoUnit.SECONDS)
        );

        when(otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc(sampleMobile)).thenReturn(Optional.of(activeMetadata));

        assertThatThrownBy(() -> smsOtpService.generateAndSendOtp(sampleMobile))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Please wait");
    }

    @Test
    void verifyOtp_ShouldMarkVerified_WhenValidOtpProvided() throws Exception {
        String plainOtp = "123456";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String hash = HexFormat.of().formatHex(digest.digest(plainOtp.getBytes(StandardCharsets.UTF_8)));

        Instant now = Instant.now();
        OtpMetadataEntity metadata = new OtpMetadataEntity(
                sampleMobile,
                hash,
                now.plus(5, ChronoUnit.MINUTES),
                now.minus(1, ChronoUnit.MINUTES)
        );

        when(otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc(sampleMobile)).thenReturn(Optional.of(metadata));

        boolean result = smsOtpService.verifyOtp(sampleMobile, plainOtp);

        assertThat(result).isTrue();
        assertThat(metadata.isVerified()).isTrue();
        verify(otpMetadataRepository).save(metadata);
    }

    @Test
    void verifyOtp_ShouldThrowValidationException_WhenMaxAttemptsExceeded() {
        Instant now = Instant.now();
        OtpMetadataEntity metadata = new OtpMetadataEntity(
                sampleMobile,
                "somehash",
                now.plus(5, ChronoUnit.MINUTES),
                now.minus(1, ChronoUnit.MINUTES)
        );
        metadata.setAttemptsCount(3);

        when(otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc(sampleMobile)).thenReturn(Optional.of(metadata));

        assertThatThrownBy(() -> smsOtpService.verifyOtp(sampleMobile, "123456"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Maximum OTP verification attempts exceeded");
    }
}
