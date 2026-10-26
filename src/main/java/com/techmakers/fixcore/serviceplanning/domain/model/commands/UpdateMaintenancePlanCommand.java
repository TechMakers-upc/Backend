package com.techmakers.fixcore.serviceplanning.domain.model.commands;

import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;

public record UpdateMaintenancePlanCommand(Long planId, PlanDetails details) {

    public UpdateMaintenancePlanCommand {
        if (planId == null || planId <= 0) throw new IllegalArgumentException("Plan identifier must be a positive number");
        if (details == null) throw new IllegalArgumentException("Plan details are required");
    }
}
