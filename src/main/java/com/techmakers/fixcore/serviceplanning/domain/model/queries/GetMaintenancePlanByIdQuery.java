package com.techmakers.fixcore.serviceplanning.domain.model.queries;

public record GetMaintenancePlanByIdQuery(Long planId) {

    public GetMaintenancePlanByIdQuery {
        if (planId == null || planId <= 0) throw new IllegalArgumentException("Plan identifier must be a positive number");
    }
}
