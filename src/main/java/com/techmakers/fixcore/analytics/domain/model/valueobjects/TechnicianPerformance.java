package com.techmakers.fixcore.analytics.domain.model.valueobjects;

import java.time.Instant;

public record TechnicianPerformance(Long technicianId, long assigned, long inProgress, long completed, long overdue,
                                    Double avgRepairHours, Instant lastActivity) {
}
