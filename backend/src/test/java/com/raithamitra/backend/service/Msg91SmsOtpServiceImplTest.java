package com.raithamitra.backend.service;

import com.raithamitra.backend.entity.OtpMetadataEntity;
import com.raithamitra.backend.exception.ValidationException;
import com.raithamitra.backend.repository.OtpMetadataRepository;
import com.raithamitra.backend.service.impl.Msg91SmsOtpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit test suite verifying Msg91SmsOtpServiceImpl HTTPS REST client communication,
 * phone number E.164 normalization, fail-fast configuration checks, and error mapping.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class Msg91SmsOtpServiceImplTest {

    @Mock
    private OtpMetadataRepository otpMetadataRepository;

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer mockServer;
    private Msg91SmsOtpServiceImpl msg91SmsOtpService;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        RestClient restClient = restClientBuilder.build();

        msg91SmsOtpService = new Msg91SmsOtpServiceImpl(otpMetadataRepository, restClient);
        ReflectionTestUtils.setField(msg91SmsOtpService, "authKey", "test-auth-key-12345");
        ReflectionTestUtils.setField(msg91SmsOtpService, "templateId", "test-template-id-67890");
        ReflectionTestUtils.setField(msg91SmsOtpService, "senderId", "RAITHA");
        ReflectionTestUtils.setField(msg91SmsOtpService, "apiUrl", "https://control.msg91.com/api/v5/flow/");
    }

    @Test
    void validateProductionConfiguration_whenMissingCredentials_throwsIllegalStateException() {
        Msg91SmsOtpServiceImpl unconfiguredService = new Msg91SmsOtpServiceImpl(otpMetadataRepository);
        ReflectionTestUtils.setField(unconfiguredService, "authKey", "");
        ReflectionTestUtils.setField(unconfiguredService, "templateId", "");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                unconfiguredService::validateProductionConfiguration
        );
        assertTrue(ex.getMessage().contains("requires 'app.msg91.auth-key'"));
    }

    @Test
    void generateAndSendOtp_successfulHttp200_dispatchesSmsAndSavesEntity() {
        mockServer.expect(requestTo("https://control.msg91.com/api/v5/flow/"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("authkey", "test-auth-key-12345"))
                .andExpect(header(org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess("{\"type\":\"success\",\"message\":\"OTP sent\"}", MediaType.APPLICATION_JSON));

        when(otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc("+919876543210"))
                .thenReturn(Optional.empty());

        msg91SmsOtpService.generateAndSendOtp("+919876543210");

        mockServer.verify();
        ArgumentCaptor<OtpMetadataEntity> entityCaptor = ArgumentCaptor.forClass(OtpMetadataEntity.class);
        verify(otpMetadataRepository, times(1)).save(entityCaptor.capture());

        OtpMetadataEntity saved = entityCaptor.getValue();
        assertEquals("+919876543210", saved.getMobileNumber());
        assertNotNull(saved.getOtpHash());
        assertFalse(saved.isVerified());
    }

    @Test
    void generateAndSendOtp_provider401Unauthorized_throwsValidationException() {
        mockServer.expect(requestTo("https://control.msg91.com/api/v5/flow/"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        when(otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc("9876543210"))
                .thenReturn(Optional.empty());

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> msg91SmsOtpService.generateAndSendOtp("9876543210")
        );

        assertTrue(ex.getMessage().contains("invalid provider authentication configuration"));
        verify(otpMetadataRepository, never()).save(any());
    }

    @Test
    void generateAndSendOtp_provider429RateLimited_throwsValidationException() {
        mockServer.expect(requestTo("https://control.msg91.com/api/v5/flow/"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        when(otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc("9876543210"))
                .thenReturn(Optional.empty());

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> msg91SmsOtpService.generateAndSendOtp("9876543210")
        );

        assertTrue(ex.getMessage().contains("rate limit exceeded"));
    }

    @Test
    void generateAndSendOtp_provider500ServerError_throwsValidationException() {
        mockServer.expect(requestTo("https://control.msg91.com/api/v5/flow/"))
                .andRespond(withServerError());

        when(otpMetadataRepository.findTopByMobileNumberOrderByCreatedAtDesc("9876543210"))
                .thenReturn(Optional.empty());

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> msg91SmsOtpService.generateAndSendOtp("9876543210")
        );

        assertTrue(ex.getMessage().contains("currently unavailable"));
    }

    @Test
    void normalizePhoneNumber_validIndianNumbers_returnsCanonical12DigitNumber() {
        assertEquals("919876543210", msg91SmsOtpService.normalizePhoneNumber("+919876543210"));
        assertEquals("919876543210", msg91SmsOtpService.normalizePhoneNumber("9876543210"));
        assertEquals("919876543210", msg91SmsOtpService.normalizePhoneNumber("919876543210"));
        assertEquals("919876543210", msg91SmsOtpService.normalizePhoneNumber("+91 98765 43210"));
    }

    @Test
    void normalizePhoneNumber_invalidNumbers_throwsValidationException() {
        assertThrows(ValidationException.class, () -> msg91SmsOtpService.normalizePhoneNumber("12345"));
        assertThrows(ValidationException.class, () -> msg91SmsOtpService.normalizePhoneNumber("5876543210"));
        assertThrows(ValidationException.class, () -> msg91SmsOtpService.normalizePhoneNumber(""));
        assertThrows(ValidationException.class, () -> msg91SmsOtpService.normalizePhoneNumber(null));
    }
}
