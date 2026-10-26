package com.techmakers.fixcore.serviceplanning.domain.model.commands;

import java.time.LocalDate;

public record RescheduleMaintenancePlanCommand(Long planId, LocalDate nextDueDate) {

    public RescheduleMaintenancePlanCommand {
        if (planId == null || planId <= 0) throw new IllegalArgumentException("Plan identifier must be a positive number");
        if (nextDueDate == null) throw new IllegalArgumentException("Next due date is required");
    }
}
