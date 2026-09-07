package com.raithamitra.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration Test verifying Spring Boot application context initialization.
 * Uses H2 in-memory test configuration via 'test' profile setup.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
class RaithaMitraApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring ApplicationContext starts cleanly
    }
}
