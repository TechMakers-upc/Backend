package com.techmakers.fixcore.analytics.domain.model.valueobjects;

import java.time.Instant;
import java.time.LocalDate;

public record OrderRecord(Long id, Long plantId, Long technicianId, OrderType type, OrderStatus status,
                          boolean critical, LocalDate dueDate, Instant createdAt, Instant completedAt,
                          Double repairHours, Instant lastActivity) {

    public boolean isOpen() {
        return status != OrderStatus.COMPLETED;
    }

    public boolean isOverdue(LocalDate today) {
        return isOpen() && dueDate.isBefore(today);
    }
}
