package com.techmakers.fixcore.shared.infrastructure.persistence.jpa.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@ConditionalOnProperty(name = "fixcore.jpa.auditing.enabled", havingValue = "true", matchIfMissing = true)
@EnableJpaAuditing
public class JpaAuditingConfiguration {
}
