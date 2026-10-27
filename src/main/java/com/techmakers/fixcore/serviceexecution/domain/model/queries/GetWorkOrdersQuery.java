package com.techmakers.fixcore.serviceexecution.domain.model.queries;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderStatus;

public record GetWorkOrdersQuery(Long plantId, Long assetId, Long technicianId, WorkOrderStatus status) {
}
