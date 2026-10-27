package com.techmakers.fixcore.serviceexecution.interfaces.rest.resources;

import java.time.Instant;

public record FailureResource(
        Long id,
        Long plantId,
        Long assetId,
        String kind,
        String description,
        boolean lineStopped,
        String priority,
        String status,
        Long reportedBy,
        Instant reportedAt,
        Instant resolvedAt,
        Long workOrderId,
        double downtimeHours,
        boolean needsWorkOrder
) {
}
