package com.techmakers.fixcore.serviceexecution.domain.model.commands;

public record StartWorkOrderCommand(Long workOrderId) {

    public StartWorkOrderCommand {
        if (workOrderId == null || workOrderId <= 0) throw new IllegalArgumentException("Work order identifier must be a positive number");
    }
}
