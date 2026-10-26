package com.techmakers.fixcore.serviceplanning.domain.model.queries;

public record GetMaintenancePlansByPlantIdQuery(Long plantId) {

    public GetMaintenancePlansByPlantIdQuery {
        if (plantId == null || plantId <= 0) throw new IllegalArgumentException("Plant identifier must be a positive number");
    }
}
