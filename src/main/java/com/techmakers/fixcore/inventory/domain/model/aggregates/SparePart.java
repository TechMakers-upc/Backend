package com.techmakers.fixcore.inventory.domain.model.aggregates;

import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.StockLevel;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;

public class SparePart {

    private final Long id;
    private final Long plantId;
    private SparePartDetails details;
    private int stock;

    public SparePart(Long plantId, SparePartDetails details) {
        this(null, plantId, details, 0);
    }

    public SparePart(Long id, Long plantId, SparePartDetails details, int stock) {
        if (plantId == null) throw new IllegalArgumentException("A part must belong to a plant");
        if (details == null) throw new IllegalArgumentException("Part details are required");
        if (stock < 0) throw new IllegalArgumentException("Quantities are whole, non-negative numbers");
        this.id = id;
        this.plantId = plantId;
        this.details = details;
        this.stock = stock;
    }

    public StockLevel level() {
        if (stock <= 0) return StockLevel.OUT;
        return stock <= details.minStock() ? StockLevel.LOW : StockLevel.OK;
    }

    public boolean isBelowMinimum() {
        return level() != StockLevel.OK;
    }

    public void updateDetails(SparePartDetails details) {
        if (details == null) throw new IllegalArgumentException("Part details are required");
        this.details = details;
    }

    public void receive(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Entries must add at least one unit");
        stock += quantity;
    }

    public int adjustTo(int counted) {
        if (counted < 0) throw new IllegalArgumentException("Quantities are whole, non-negative numbers");
        var delta = counted - stock;
        stock = counted;
        return delta;
    }

    public void consume(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Consumption must be at least one unit");
        if (quantity > stock) throw new BusinessRuleViolationException("Not enough stock");
        stock -= quantity;
    }

    public Long getId() { return id; }

    public Long getPlantId() { return plantId; }

    public SparePartDetails getDetails() { return details; }

    public int getStock() { return stock; }
}
