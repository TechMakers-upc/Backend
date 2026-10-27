package com.techmakers.fixcore.inventory.domain.model.commands;

public record ConsumeStockCommand(Long partId, int quantity, Long workOrderId, String reason, String performedBy) {

    public ConsumeStockCommand {
        if (partId == null || partId <= 0) throw new IllegalArgumentException("Part identifier must be a positive number");
        if (performedBy == null || performedBy.isBlank()) throw new IllegalArgumentException("The user is required");
    }
}
