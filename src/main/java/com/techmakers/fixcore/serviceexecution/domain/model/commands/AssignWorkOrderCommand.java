package com.techmakers.fixcore.serviceexecution.domain.model.commands;

public record AssignWorkOrderCommand(Long workOrderId, Long technicianId) {

    public AssignWorkOrderCommand {
        if (workOrderId == null || workOrderId <= 0) throw new IllegalArgumentException("Work order identifier must be a positive number");
        if (technicianId == null) throw new IllegalArgumentException("Technician is required");
    }
}
