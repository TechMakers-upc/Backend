package com.techmakers.fixcore.serviceplanning.interfaces.rest.resources;

import java.time.LocalDate;

public record MaintenancePlanResource(
        Long id,
        Long plantId,
        Long assetId,
        String title,
        String tasks,
        int frequencyDays,
        LocalDate nextDueDate,
        LocalDate lastCompletedAt,
        boolean active,
        Long technicianId,
        String standing
) {
}
