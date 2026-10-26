package com.techmakers.fixcore.serviceplanning.domain.model.commands;

import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;

public record CreateMaintenancePlanCommand(PlanDetails details) {

    public CreateMaintenancePlanCommand {
        if (details == null) throw new IllegalArgumentException("Plan details are required");
    }
}
