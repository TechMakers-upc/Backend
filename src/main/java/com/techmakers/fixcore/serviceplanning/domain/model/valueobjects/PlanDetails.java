package com.techmakers.fixcore.serviceplanning.domain.model.valueobjects;

import java.time.LocalDate;

public record PlanDetails(
        Long assetId,
        String title,
        String tasks,
        int frequencyDays,
        LocalDate nextDueDate,
        Long technicianId
) {

    public static final int MIN_FREQUENCY_DAYS = 1;
    public static final int MAX_FREQUENCY_DAYS = 730;

    public PlanDetails {
        if (assetId == null || title == null || title.isBlank()) {
            throw new IllegalArgumentException("A plan needs an asset and a title");
        }
        if (frequencyDays < MIN_FREQUENCY_DAYS || frequencyDays > MAX_FREQUENCY_DAYS) {
            throw new IllegalArgumentException("Frequency must be 1 to 730 days");
        }
        if (nextDueDate == null) throw new IllegalArgumentException("Next due date is required");
        title = title.trim();
        tasks = tasks == null ? "" : tasks.trim();
    }
}
