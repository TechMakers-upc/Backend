package com.techmakers.fixcore.serviceplanning.domain.model.commands;

import java.time.LocalDate;

public record RegisterPlanCompletionCommand(Long planId, LocalDate completedOn) {

    public RegisterPlanCompletionCommand {
        if (planId == null || planId <= 0) throw new IllegalArgumentException("Plan identifier must be a positive number");
        if (completedOn == null) throw new IllegalArgumentException("Completion date is required");
    }
}
