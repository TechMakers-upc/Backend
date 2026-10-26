package com.techmakers.fixcore.serviceplanning.domain.model.commands;

public record ReactivateMaintenancePlanCommand(Long planId) {

    public ReactivateMaintenancePlanCommand {
        if (planId == null || planId <= 0) throw new IllegalArgumentException("Plan identifier must be a positive number");
    }
}
