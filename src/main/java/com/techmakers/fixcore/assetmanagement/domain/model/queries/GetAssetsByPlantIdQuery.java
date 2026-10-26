package com.techmakers.fixcore.assetmanagement.domain.model.queries;

public record GetAssetsByPlantIdQuery(Long plantId) {

    public GetAssetsByPlantIdQuery {
        if (plantId == null || plantId <= 0) throw new IllegalArgumentException("Plant identifier must be a positive number");
    }
}
