package com.techmakers.fixcore.inventory.interfaces.rest.transform;

import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.StockMovementResource;
import com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler;

public final class StockMovementResourceAssembler {

    private StockMovementResourceAssembler() {
    }

    public static StockMovementResource toResource(StockMovement movement) {
        return new StockMovementResource(movement.id(), movement.plantId(), movement.partId(),
                EnumValueAssembler.toValue(movement.type()), movement.quantity(), movement.stockAfter(),
                movement.reason(), movement.at(), movement.userId(), movement.workOrderId());
    }
}
