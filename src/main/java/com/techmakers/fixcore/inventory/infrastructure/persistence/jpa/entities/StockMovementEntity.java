package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.entities;

import com.techmakers.fixcore.inventory.domain.model.valueobjects.MovementType;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;

@Entity(name = "StockMovement")
public class StockMovementEntity extends AuditableEntity {

    @Column(nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private Long partId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int stockAfter;

    @Column(nullable = false, length = 200)
    private String reason;

    @Column(nullable = false)
    private Instant occurredAt;

    private Long userId;

    private Long workOrderId;

    protected StockMovementEntity() {
    }

    public StockMovementEntity(Long plantId, Long partId, MovementType type, int quantity, int stockAfter,
                               String reason, Instant occurredAt, Long userId, Long workOrderId) {
        this.plantId = plantId;
        this.partId = partId;
        this.type = type;
        this.quantity = quantity;
        this.stockAfter = stockAfter;
        this.reason = reason;
        this.occurredAt = occurredAt;
        this.userId = userId;
        this.workOrderId = workOrderId;
    }

    public Long getPlantId() { return plantId; }
    public Long getPartId() { return partId; }
    public MovementType getType() { return type; }
    public int getQuantity() { return quantity; }
    public int getStockAfter() { return stockAfter; }
    public String getReason() { return reason; }
    public Instant getOccurredAt() { return occurredAt; }
    public Long getUserId() { return userId; }
    public Long getWorkOrderId() { return workOrderId; }
}
