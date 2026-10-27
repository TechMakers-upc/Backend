package com.techmakers.fixcore.serviceexecution.interfaces.acl;

import java.time.Instant;
import java.time.LocalDate;

public record WorkOrderSummary(Long id, String code, Long plantId, Long assetId, Long technicianId, String type,
                               String status, String priority, LocalDate dueDate, Instant createdAt,
                               Instant completedAt, Double repairHours, Instant lastActivity) {
}
