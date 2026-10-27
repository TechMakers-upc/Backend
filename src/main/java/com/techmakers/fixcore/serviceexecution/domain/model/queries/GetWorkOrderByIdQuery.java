package com.techmakers.fixcore.serviceexecution.domain.model.queries;

public record GetWorkOrderByIdQuery(Long workOrderId) {

    public GetWorkOrderByIdQuery {
        if (workOrderId == null || workOrderId <= 0) throw new IllegalArgumentException("Work order identifier must be a positive number");
    }
}
