package com.techmakers.fixcore.serviceexecution.interfaces.acl;

import java.time.Instant;

public record FailureSummary(Long id, Long plantId, Long assetId, String priority, boolean resolved,
                             double downtimeHours, Instant reportedAt) {
}
