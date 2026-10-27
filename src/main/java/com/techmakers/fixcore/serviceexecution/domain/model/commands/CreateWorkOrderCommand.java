package com.techmakers.fixcore.serviceexecution.domain.model.commands;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderDetails;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderType;

public record CreateWorkOrderCommand(WorkOrderType type, Long assetId, WorkOrderDetails details, Long technicianId,
                                     Long failureId, Long planId, String createdBy) {

    public CreateWorkOrderCommand {
        if (type == null) throw new IllegalArgumentException("Work order type is required");
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
        if (details == null) throw new IllegalArgumentException("Work order details are required");
        if (createdBy == null || createdBy.isBlank()) throw new IllegalArgumentException("The creator is required");
    }
}
