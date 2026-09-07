package com.raithamitra.backend.service;

/**
 * Service interface for OTP generation, delivery abstraction, and cryptographic verification.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public interface SmsOtpService {

    /**
     * Generates a 6-digit cryptographically secure OTP, hashes it, stores lifecycle metadata,
     * and triggers SMS delivery to the target mobile number.
     *
     * @param mobileNumber the target Indian mobile number
     */
    void generateAndSendOtp(String mobileNumber);

    /**
     * Verifies the submitted OTP against stored metadata, enforcing expiration, attempt limits,
     * and single-use verification rules.
     *
     * @param mobileNumber target mobile number
     * @param otp 6-digit plain text OTP
     * @return true if verification succeeds
     */
    boolean verifyOtp(String mobileNumber, String otp);
}
