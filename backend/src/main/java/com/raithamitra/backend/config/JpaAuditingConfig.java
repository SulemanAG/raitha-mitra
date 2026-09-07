package com.raithamitra.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing Configuration enabling automatic creation and modification timestamping.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
