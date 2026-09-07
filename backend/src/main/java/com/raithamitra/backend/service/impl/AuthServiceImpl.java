package com.raithamitra.backend.service.impl;

import com.raithamitra.backend.dto.request.RequestOtpRequestDto;
import com.raithamitra.backend.dto.request.VerifyOtpRequestDto;
import com.raithamitra.backend.dto.response.AuthSessionResponseDto;
import com.raithamitra.backend.dto.response.UserResponseDto;
import com.raithamitra.backend.entity.AccountStatus;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import com.raithamitra.backend.exception.ResourceNotFoundException;
import com.raithamitra.backend.repository.UserRepository;
import com.raithamitra.backend.security.JwtTokenProvider;
import com.raithamitra.backend.security.SecurityUser;
import com.raithamitra.backend.security.SecurityUtils;
import com.raithamitra.backend.service.AuthService;
import com.raithamitra.backend.service.SmsOtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation managing OTP validation, seamless user auto-provisioning,
 * JWT authentication session issuance, and active security context state retrieval.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final SmsOtpService smsOtpService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final long jwtExpirationMs;
    private final boolean otpEnabled;

    public AuthServiceImpl(
            SmsOtpService smsOtpService,
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            @Value("${app.jwt.expiration-ms:86400000}") long jwtExpirationMs,
            @Value("${app.auth.otp.enabled:false}") boolean otpEnabled
    ) {
        this.smsOtpService = smsOtpService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtExpirationMs = jwtExpirationMs;
        this.otpEnabled = otpEnabled;
    }

    @Override
    public void requestOtp(RequestOtpRequestDto requestDto) {
        if (otpEnabled) {
            log.info("Requesting OTP for mobile number: {}", maskMobile(requestDto.mobileNumber()));
            smsOtpService.generateAndSendOtp(requestDto.mobileNumber());
        } else {
            log.info("[DEMO AUTH MODE] OTP challenge disabled (app.auth.otp.enabled=false). Bypassing SMS dispatch for mobile: {}", maskMobile(requestDto.mobileNumber()));
        }
    }

    @Override
    @Transactional
    public AuthSessionResponseDto verifyOtpAndAuthenticate(VerifyOtpRequestDto requestDto) {
        if (otpEnabled) {
            log.info("Verifying OTP for mobile number: {}", maskMobile(requestDto.mobileNumber()));
            smsOtpService.verifyOtp(requestDto.mobileNumber(), requestDto.otp());
        } else {
            log.info("[DEMO AUTH MODE] OTP challenge disabled (app.auth.otp.enabled=false). Authenticating user directly for mobile: {}", maskMobile(requestDto.mobileNumber()));
        }

        boolean isNewUser = false;
        Optional<UserEntity> existingUser = userRepository.findByMobileNumber(requestDto.mobileNumber());

        UserEntity user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (!user.isActive()) {
                throw new com.raithamitra.backend.exception.ValidationException("Account is inactive or suspended");
            }
        } else {
            isNewUser = true;
            Set<UserRole> roles = new HashSet<>();
            roles.add(UserRole.FARMER);

            user = UserEntity.builder()
                    .mobileNumber(requestDto.mobileNumber())
                    .primaryRole("FARMER")
                    .accountStatus(AccountStatus.ACTIVE)
                    .roles(roles)
                    .build();

            user = userRepository.save(user);
            log.info("Auto-provisioned new user account with ID: {} for mobile: {}", user.getId(), maskMobile(user.getMobileNumber()));
        }

        String accessToken = jwtTokenProvider.generateTokenFromUserIdAndRole(
                user.getId(),
                user.getMobileNumber(),
                user.getPrimaryRole()
        );

        UserResponseDto userDto = mapToUserResponseDto(user);
        return new AuthSessionResponseDto(accessToken, jwtExpirationMs / 1000, userDto, isNewUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {
        SecurityUser securityUser = SecurityUtils.getCurrentUser();
        UserEntity user = userRepository.findById(securityUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", securityUser.getUserId()));
        return mapToUserResponseDto(user);
    }

    private UserResponseDto mapToUserResponseDto(UserEntity entity) {
        Set<String> roleNames = entity.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new UserResponseDto(
                entity.getId(),
                entity.getMobileNumber(),
                entity.getPrimaryRole(),
                entity.getAccountStatus().name(),
                roleNames,
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String maskMobile(String mobile) {
        if (mobile != null && mobile.length() >= 10) {
            return mobile.substring(0, 5) + "*****" + mobile.substring(mobile.length() - 2);
        }
        return "*****";
    }
}
