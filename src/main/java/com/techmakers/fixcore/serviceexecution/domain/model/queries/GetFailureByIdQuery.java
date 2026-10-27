package com.techmakers.fixcore.serviceexecution.domain.model.queries;

public record GetFailureByIdQuery(Long failureId) {

    public GetFailureByIdQuery {
        if (failureId == null || failureId <= 0) throw new IllegalArgumentException("Failure identifier must be a positive number");
    }
}
