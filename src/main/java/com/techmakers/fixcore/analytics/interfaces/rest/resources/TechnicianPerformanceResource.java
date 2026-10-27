package com.techmakers.fixcore.analytics.interfaces.rest.resources;

import java.time.Instant;

public record TechnicianPerformanceResource(Long technicianId, long assigned, long inProgress, long completed,
                                            long overdue, Double avgRepairHours, Instant lastActivity) {
}
