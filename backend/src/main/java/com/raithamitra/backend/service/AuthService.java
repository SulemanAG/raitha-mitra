package com.raithamitra.backend.service;

import com.raithamitra.backend.dto.request.RequestOtpRequestDto;
import com.raithamitra.backend.dto.request.VerifyOtpRequestDto;
import com.raithamitra.backend.dto.response.AuthSessionResponseDto;
import com.raithamitra.backend.dto.response.UserResponseDto;

/**
 * Service interface managing user authentication lifecycle, OTP verification flow,
 * and security session creation.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface AuthService {

    /**
     * Initiates OTP generation and delivery for a mobile number.
     *
     * @param requestDto DTO containing mobile number
     */
    void requestOtp(RequestOtpRequestDto requestDto);

    /**
     * Verifies OTP, auto-provisions user account if new, generates JWT, and opens authentication session.
     *
     * @param requestDto DTO containing mobile number and plain text OTP
     * @return AuthSessionResponseDto with JWT token and user details
     */
    AuthSessionResponseDto verifyOtpAndAuthenticate(VerifyOtpRequestDto requestDto);

    /**
     * Retrieves current authenticated user profile details from security context.
     *
     * @return UserResponseDto of the currently logged in user
     */
    UserResponseDto getCurrentUser();
}
