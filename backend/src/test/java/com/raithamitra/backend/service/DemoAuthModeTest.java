package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.RequestOtpRequestDto;
import com.raithamitra.backend.dto.request.VerifyOtpRequestDto;
import com.raithamitra.backend.dto.response.AuthSessionResponseDto;
import com.raithamitra.backend.entity.AccountStatus;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.exception.ValidationException;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.JwtTokenProvider;
import com.raithamitra.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test suite verifying Demo Authentication Mode behavior (app.auth.otp.enabled=false)
 * and OTP-enabled Mode behavior (app.auth.otp.enabled=true).
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class DemoAuthModeTest {

    @Mock
    private SmsOtpService smsOtpService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthServiceImpl demoAuthService;
    private AuthServiceImpl otpEnabledAuthService;

    @BeforeEach
    void setUp() {
        demoAuthService = new AuthServiceImpl(smsOtpService, userRepository, jwtTokenProvider, 86400000L, false);
        otpEnabledAuthService = new AuthServiceImpl(smsOtpService, userRepository, jwtTokenProvider, 86400000L, true);
    }

    @Test
    void requestOtp_demoMode_bypassesSmsOtpServiceDispatch() {
        RequestOtpRequestDto dto = new RequestOtpRequestDto("+919876543210");
        demoAuthService.requestOtp(dto);
        verify(smsOtpService, never()).generateAndSendOtp(any());
    }

    @Test
    void verifyOtpAndAuthenticate_demoMode_provisionsNewUserWithoutOtpCheck() {
        VerifyOtpRequestDto dto = new VerifyOtpRequestDto("+919876543210", "000000");

        when(userRepository.findByMobileNumber("+919876543210")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity u = inv.getArgument(0);
            u.setId(java.util.UUID.randomUUID());
            return u;
        });
        when(jwtTokenProvider.generateTokenFromUserIdAndRole(any(), eq("+919876543210"), eq("FARMER")))
                .thenReturn("mock-jwt-token-12345");

        AuthSessionResponseDto response = demoAuthService.verifyOtpAndAuthenticate(dto);

        assertNotNull(response);
        assertEquals("mock-jwt-token-12345", response.accessToken());
        assertTrue(response.isNewUser());
        verify(smsOtpService, never()).verifyOtp(any(), any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void verifyOtpAndAuthenticate_demoMode_authenticatesExistingUserWithoutOtpCheck() {
        VerifyOtpRequestDto dto = new VerifyOtpRequestDto("+919876543210", "000000");
        UserEntity existing = UserEntity.builder()
                .mobileNumber("+919876543210")
                .primaryRole("FARMER")
                .accountStatus(AccountStatus.ACTIVE)
                .roles(Set.of(UserRole.FARMER))
                .build();
        existing.setId(java.util.UUID.randomUUID());

        when(userRepository.findByMobileNumber("+919876543210")).thenReturn(Optional.of(existing));
        when(jwtTokenProvider.generateTokenFromUserIdAndRole(eq(existing.getId()), eq("+919876543210"), eq("FARMER")))
                .thenReturn("existing-jwt-token");

        AuthSessionResponseDto response = demoAuthService.verifyOtpAndAuthenticate(dto);

        assertNotNull(response);
        assertEquals("existing-jwt-token", response.accessToken());
        assertFalse(response.isNewUser());
        verify(smsOtpService, never()).verifyOtp(any(), any());
    }

    @Test
    void verifyOtpAndAuthenticate_demoMode_rejectsInactiveOrSuspendedAccount() {
        VerifyOtpRequestDto dto = new VerifyOtpRequestDto("+919876543210", "000000");
        UserEntity suspendedUser = UserEntity.builder()
                .mobileNumber("+919876543210")
                .primaryRole("FARMER")
                .accountStatus(AccountStatus.SUSPENDED)
                .roles(Set.of(UserRole.FARMER))
                .build();
        suspendedUser.setId(java.util.UUID.randomUUID());

        when(userRepository.findByMobileNumber("+919876543210")).thenReturn(Optional.of(suspendedUser));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> demoAuthService.verifyOtpAndAuthenticate(dto)
        );

        assertTrue(ex.getMessage().contains("inactive or suspended"));
    }

    @Test
    void verifyOtpAndAuthenticate_otpEnabledMode_enforcesSmsOtpServiceVerification() {
        VerifyOtpRequestDto dto = new VerifyOtpRequestDto("+919876543210", "123456");

        when(smsOtpService.verifyOtp("+919876543210", "123456")).thenReturn(true);
        when(userRepository.findByMobileNumber("+919876543210")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(inv -> {
            UserEntity u = inv.getArgument(0);
            u.setId(java.util.UUID.randomUUID());
            return u;
        });
        when(jwtTokenProvider.generateTokenFromUserIdAndRole(any(), any(), any()))
                .thenReturn("otp-verified-jwt");

        AuthSessionResponseDto response = otpEnabledAuthService.verifyOtpAndAuthenticate(dto);

        assertNotNull(response);
        verify(smsOtpService, times(1)).verifyOtp("+919876543210", "123456");
    }
}
