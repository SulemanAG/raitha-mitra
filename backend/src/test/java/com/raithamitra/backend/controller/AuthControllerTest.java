package com.raithamitra.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raithamitra.backend.dto.request.RequestOtpRequestDto;
import com.raithamitra.backend.dto.request.VerifyOtpRequestDto;
import com.raithamitra.backend.dto.response.AuthSessionResponseDto;
import com.raithamitra.backend.dto.response.UserResponseDto;
import com.raithamitra.backend.security.CustomUserDetailsService;
import com.raithamitra.backend.security.JwtTokenProvider;
import com.raithamitra.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WebMvcTest slice test verifying AuthController REST endpoints and request validation.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void requestOtp_ShouldReturn200_WhenValidPayload() throws Exception {
        RequestOtpRequestDto payload = new RequestOtpRequestDto("+919876543210");
        doNothing().when(authService).requestOtp(any(RequestOtpRequestDto.class));

        mockMvc.perform(post("/api/v1/auth/request-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));
    }

    @Test
    void verifyOtp_ShouldReturn200_WhenValidPayload() throws Exception {
        VerifyOtpRequestDto payload = new VerifyOtpRequestDto("+919876543210", "123456");
        UserResponseDto userDto = new UserResponseDto(
                UUID.randomUUID(), "+919876543210", "FARMER", "ACTIVE", Set.of("FARMER"), true, Instant.now(), Instant.now()
        );
        AuthSessionResponseDto sessionDto = new AuthSessionResponseDto("mockJwtToken", 86400, userDto, false);

        when(authService.verifyOtpAndAuthenticate(any(VerifyOtpRequestDto.class))).thenReturn(sessionDto);

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockJwtToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}
