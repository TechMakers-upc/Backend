package com.techmakers.fixcore.serviceexecution.interfaces.rest.resources;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record WorkOrderResource(
        Long id,
        String code,
        String type,
        Long plantId,
        Long assetId,
        Long failureId,
        Long planId,
        String title,
        String description,
        String priority,
        String status,
        Long technicianId,
        LocalDate dueDate,
        Instant createdAt,
        Long createdBy,
        Instant startedAt,
        Instant completedAt,
        String solution,
        List<WorkLogEntryResource> workLog,
        List<PartUsageResource> partsUsed,
        boolean overdue,
        Double repairHours
) {

    public record WorkLogEntryResource(Instant at, Long authorId, String note) {
    }

    public record PartUsageResource(Long partId, int quantity, Instant at) {
    }
}
