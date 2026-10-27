package com.techmakers.fixcore.inventory.domain.model.queries;

public record GetSparePartsByPlantIdQuery(Long plantId) {

    public GetSparePartsByPlantIdQuery {
        if (plantId == null || plantId <= 0) throw new IllegalArgumentException("Plant identifier must be a positive number");
    }
}
