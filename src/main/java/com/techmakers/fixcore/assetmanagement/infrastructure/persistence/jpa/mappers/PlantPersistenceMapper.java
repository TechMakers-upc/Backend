package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.mappers;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.entities.PlantEntity;

public final class PlantPersistenceMapper {

    private PlantPersistenceMapper() {
    }

    public static PlantEntity toEntity(Plant plant) {
        var entity = new PlantEntity(plant.getName(), plant.getCity(), plant.getAddress());
        entity.setId(plant.getId());
        return entity;
    }

    public static Plant toDomain(PlantEntity entity) {
        return new Plant(entity.getId(), entity.getName(), entity.getCity(), entity.getAddress());
    }
}
