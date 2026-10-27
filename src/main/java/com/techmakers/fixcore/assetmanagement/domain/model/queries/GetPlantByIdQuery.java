package com.techmakers.fixcore.assetmanagement.domain.model.queries;

public record GetPlantByIdQuery(Long plantId) {

    public GetPlantByIdQuery {
        if (plantId == null || plantId <= 0) throw new IllegalArgumentException("Plant identifier must be a positive number");
    }
}
