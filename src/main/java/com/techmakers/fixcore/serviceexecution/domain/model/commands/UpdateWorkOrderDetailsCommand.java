package com.techmakers.fixcore.serviceexecution.domain.model.commands;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderDetails;

public record UpdateWorkOrderDetailsCommand(Long workOrderId, WorkOrderDetails details) {

    public UpdateWorkOrderDetailsCommand {
        if (workOrderId == null || workOrderId <= 0) throw new IllegalArgumentException("Work order identifier must be a positive number");
        if (details == null) throw new IllegalArgumentException("Work order details are required");
    }
}
