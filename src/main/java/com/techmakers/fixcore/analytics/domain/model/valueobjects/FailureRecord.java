package com.techmakers.fixcore.analytics.domain.model.valueobjects;

import java.time.Instant;

public record FailureRecord(Long id, Long plantId, Long assetId, boolean critical, boolean resolved,
                            double downtimeHours, Instant reportedAt) {
}
