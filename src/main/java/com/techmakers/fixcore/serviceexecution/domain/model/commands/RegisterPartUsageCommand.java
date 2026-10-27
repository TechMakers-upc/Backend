package com.techmakers.fixcore.serviceexecution.domain.model.commands;

public record RegisterPartUsageCommand(Long workOrderId, Long partId, int quantity) {

    public RegisterPartUsageCommand {
        if (workOrderId == null || workOrderId <= 0) throw new IllegalArgumentException("Work order identifier must be a positive number");
        if (partId == null) throw new IllegalArgumentException("Spare part is required");
    }
}
