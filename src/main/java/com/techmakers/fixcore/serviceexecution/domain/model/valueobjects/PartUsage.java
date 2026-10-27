package com.techmakers.fixcore.serviceexecution.domain.model.valueobjects;

import java.time.Instant;

public record PartUsage(Long partId, int quantity, Instant at) {

    public PartUsage {
        if (partId == null) throw new IllegalArgumentException("Spare part is required");
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be a positive integer");
        if (at == null) throw new IllegalArgumentException("Usage date is required");
    }
}
