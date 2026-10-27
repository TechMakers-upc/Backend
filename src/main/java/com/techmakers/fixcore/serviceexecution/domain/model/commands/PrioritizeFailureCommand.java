package com.techmakers.fixcore.serviceexecution.domain.model.commands;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;

public record PrioritizeFailureCommand(Long failureId, Priority priority) {

    public PrioritizeFailureCommand {
        if (failureId == null || failureId <= 0) throw new IllegalArgumentException("Failure identifier must be a positive number");
        if (priority == null) throw new IllegalArgumentException("Priority is required");
    }
}
