package com.techmakers.fixcore.inventory.interfaces.rest.resources;

import java.time.Instant;

public record StockMovementResource(
        Long id,
        Long plantId,
        Long partId,
        String type,
        int quantity,
        int stockAfter,
        String reason,
        Instant at,
        Long userId,
        Long workOrderId
) {
}
