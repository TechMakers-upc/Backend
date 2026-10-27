package com.techmakers.fixcore.inventory.domain.model.aggregates;

import com.techmakers.fixcore.inventory.domain.model.valueobjects.MovementType;

import java.time.Instant;

public record StockMovement(
        Long id,
        Long plantId,
        Long partId,
        MovementType type,
        int quantity,
        int stockAfter,
        String reason,
        Instant at,
        Long userId,
        Long workOrderId
) {

    public StockMovement {
        if (plantId == null || partId == null) throw new IllegalArgumentException("A movement needs a plant and a part");
        if (type == null || at == null) throw new IllegalArgumentException("Movement type and date are required");
        if (stockAfter < 0) throw new IllegalArgumentException("Stock cannot be negative");
        reason = reason == null ? "" : reason.trim();
    }

    public static StockMovement entry(SparePart part, int quantity, String reason, Long userId, Instant at) {
        return of(part, MovementType.ENTRY, quantity, reason, userId, null, at);
    }

    public static StockMovement adjustment(SparePart part, int delta, String reason, Long userId, Instant at) {
        return of(part, MovementType.ADJUSTMENT, delta, reason, userId, null, at);
    }

    public static StockMovement consumption(SparePart part, int quantity, String reason, Long userId,
                                            Long workOrderId, Instant at) {
        return of(part, MovementType.CONSUMPTION, -quantity, reason, userId, workOrderId, at);
    }

    private static StockMovement of(SparePart part, MovementType type, int quantity, String reason, Long userId,
                                    Long workOrderId, Instant at) {
        return new StockMovement(null, part.getPlantId(), part.getId(), type, quantity, part.getStock(), reason, at,
                userId, workOrderId);
    }
}
