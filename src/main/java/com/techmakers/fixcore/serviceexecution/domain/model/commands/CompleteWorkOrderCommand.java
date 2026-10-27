package com.techmakers.fixcore.serviceexecution.domain.model.commands;

public record CompleteWorkOrderCommand(Long workOrderId, String solution) {

    public CompleteWorkOrderCommand {
        if (workOrderId == null || workOrderId <= 0) throw new IllegalArgumentException("Work order identifier must be a positive number");
    }
}
