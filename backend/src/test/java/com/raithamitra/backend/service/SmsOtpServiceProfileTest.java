package com.raithamitra.backend.service;

import com.raithamitra.backend.service.impl.DevelopmentSmsOtpServiceImpl;
import com.raithamitra.backend.service.impl.Msg91SmsOtpServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Spring Boot Integration Test verifying that the test environment loads DevelopmentSmsOtpServiceImpl
 * and does not load production MSG91 bean when prod profile is inactive.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
class SmsOtpServiceProfileTest {

    @Autowired
    private SmsOtpService smsOtpService;

    @Test
    void testProfile_loadsDevelopmentSmsOtpService() {
        assertNotNull(smsOtpService);
        assertInstanceOf(DevelopmentSmsOtpServiceImpl.class, smsOtpService);
    }
}
