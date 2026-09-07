package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.request.RequestOtpRequestDto;
import com.raithamitra.backend.dto.request.VerifyOtpRequestDto;
import com.raithamitra.backend.dto.response.AuthSessionResponseDto;
import com.raithamitra.backend.dto.response.UserResponseDto;
import com.raithamitra.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST Controller providing authentication endpoints for OTP requests, OTP verification,
 * JWT session issuance, active user session resolution, and logout.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final boolean otpEnabled;

    public AuthController(
            AuthService authService,
            @Value("${app.auth.otp.enabled:false}") boolean otpEnabled
    ) {
        this.authService = authService;
        this.otpEnabled = otpEnabled;
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getAuthConfig() {
        return ResponseEntity.ok(Map.of(
                "otpEnabled", otpEnabled,
                "mode", otpEnabled ? "OTP_ENABLED" : "DEMO_MODE"
        ));
    }

    @PostMapping("/request-otp")
    public ResponseEntity<Map<String, Object>> requestOtp(@Valid @RequestBody RequestOtpRequestDto requestDto) {
        authService.requestOtp(requestDto);
        return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "otpRequired", otpEnabled
        ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthSessionResponseDto> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto requestDto) {
        AuthSessionResponseDto sessionResponse = authService.verifyOtpAndAuthenticate(requestDto);
        return ResponseEntity.ok(sessionResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        UserResponseDto userDto = authService.getCurrentUser();
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
