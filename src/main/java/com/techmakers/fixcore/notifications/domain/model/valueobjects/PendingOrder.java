package com.techmakers.fixcore.notifications.domain.model.valueobjects;

import java.time.Instant;

public record PendingOrder(Long id, String code, Long assetId, Long technicianId, boolean critical, Instant createdAt) {
}
