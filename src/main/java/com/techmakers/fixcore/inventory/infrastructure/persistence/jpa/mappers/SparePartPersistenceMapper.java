package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.mappers;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;
import com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.entities.SparePartEntity;

public final class SparePartPersistenceMapper {

    private SparePartPersistenceMapper() {
    }

    public static SparePartEntity toEntity(SparePart part) {
        var details = part.getDetails();
        var entity = new SparePartEntity(part.getPlantId(), details.sku(), details.name(), details.category(),
                details.unit(), part.getStock(), details.minStock(), details.location());
        entity.setId(part.getId());
        return entity;
    }

    public static SparePart toDomain(SparePartEntity entity) {
        var details = new SparePartDetails(entity.getSku(), entity.getName(), entity.getCategory(), entity.getUnit(),
                entity.getMinStock(), entity.getLocation());
        return new SparePart(entity.getId(), entity.getPlantId(), details, entity.getStock());
    }
}
