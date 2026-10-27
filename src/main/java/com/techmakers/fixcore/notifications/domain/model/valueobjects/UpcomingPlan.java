package com.techmakers.fixcore.notifications.domain.model.valueobjects;

import java.time.LocalDate;

public record UpcomingPlan(Long id, String title, Long assetId, LocalDate nextDueDate, boolean overdue) {
}
