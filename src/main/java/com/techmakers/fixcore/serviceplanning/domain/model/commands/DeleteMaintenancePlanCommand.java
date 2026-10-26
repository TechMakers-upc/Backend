package com.techmakers.fixcore.serviceplanning.domain.model.commands;

public record DeleteMaintenancePlanCommand(Long planId) {

    public DeleteMaintenancePlanCommand {
        if (planId == null || planId <= 0) throw new IllegalArgumentException("Plan identifier must be a positive number");
    }
}
