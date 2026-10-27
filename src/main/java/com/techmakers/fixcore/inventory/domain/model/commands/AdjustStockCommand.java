package com.techmakers.fixcore.inventory.domain.model.commands;

public record AdjustStockCommand(Long partId, int countedStock, String reason, String performedBy) {

    public AdjustStockCommand {
        if (partId == null || partId <= 0) throw new IllegalArgumentException("Part identifier must be a positive number");
        if (performedBy == null || performedBy.isBlank()) throw new IllegalArgumentException("The user is required");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("An adjustment needs a reason");
    }
}
