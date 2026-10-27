package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.mappers;

import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.entities.StockMovementEntity;

public final class StockMovementPersistenceMapper {

    private StockMovementPersistenceMapper() {
    }

    public static StockMovementEntity toEntity(StockMovement movement) {
        var entity = new StockMovementEntity(movement.plantId(), movement.partId(), movement.type(), movement.quantity(),
                movement.stockAfter(), movement.reason(), movement.at(), movement.userId(), movement.workOrderId());
        entity.setId(movement.id());
        return entity;
    }

    public static StockMovement toDomain(StockMovementEntity entity) {
        return new StockMovement(entity.getId(), entity.getPlantId(), entity.getPartId(), entity.getType(),
                entity.getQuantity(), entity.getStockAfter(), entity.getReason(), entity.getOccurredAt(),
                entity.getUserId(), entity.getWorkOrderId());
    }
}
