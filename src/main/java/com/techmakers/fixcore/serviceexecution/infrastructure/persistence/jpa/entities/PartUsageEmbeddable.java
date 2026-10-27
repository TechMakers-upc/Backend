package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

@Embeddable
public class PartUsageEmbeddable {

    @Column(nullable = false)
    private Long partId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Instant occurredAt;

    protected PartUsageEmbeddable() {
    }

    public PartUsageEmbeddable(Long partId, int quantity, Instant occurredAt) {
        this.partId = partId;
        this.quantity = quantity;
        this.occurredAt = occurredAt;
    }

    public Long getPartId() { return partId; }

    public int getQuantity() { return quantity; }

    public Instant getOccurredAt() { return occurredAt; }
}
