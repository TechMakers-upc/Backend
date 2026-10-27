package com.techmakers.fixcore.serviceexecution.domain.model.commands;

public record ResolveFailureCommand(Long failureId) {

    public ResolveFailureCommand {
        if (failureId == null || failureId <= 0) throw new IllegalArgumentException("Failure identifier must be a positive number");
    }
}
