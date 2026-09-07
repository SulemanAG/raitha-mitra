package com.raithamitra.backend.controller;

import com.raithamitra.backend.dto.response.HealthResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * REST Controller providing system health and availability status checks.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @Value("${spring.application.name:raitha-mitra-backend}")
    private String appName;

    @GetMapping
    public ResponseEntity<HealthResponseDto> checkHealth() {
        HealthResponseDto health = new HealthResponseDto(
                "UP",
                appName,
                "1.0.0-SNAPSHOT",
                Instant.now()
        );
        return ResponseEntity.ok(health);
    }
}
