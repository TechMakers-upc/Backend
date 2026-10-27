package com.techmakers.fixcore.inventory.domain.model.commands;

public record ReceiveStockCommand(Long partId, int quantity, String reason, String performedBy) {

    public ReceiveStockCommand {
        if (partId == null || partId <= 0) throw new IllegalArgumentException("Part identifier must be a positive number");
        if (performedBy == null || performedBy.isBlank()) throw new IllegalArgumentException("The user is required");
    }
}
