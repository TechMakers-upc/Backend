package com.techmakers.fixcore.serviceexecution.domain.model.commands;

public record DeleteWorkOrderCommand(Long workOrderId) {

    public DeleteWorkOrderCommand {
        if (workOrderId == null || workOrderId <= 0) throw new IllegalArgumentException("Work order identifier must be a positive number");
    }
}
