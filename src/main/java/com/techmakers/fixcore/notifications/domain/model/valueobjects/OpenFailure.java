package com.techmakers.fixcore.notifications.domain.model.valueobjects;

import java.time.Instant;

public record OpenFailure(Long id, Long assetId, boolean critical, Instant reportedAt) {
}
