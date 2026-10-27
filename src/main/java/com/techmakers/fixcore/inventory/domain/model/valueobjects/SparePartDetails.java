package com.techmakers.fixcore.inventory.domain.model.valueobjects;

public record SparePartDetails(
        String sku,
        String name,
        PartCategory category,
        PartUnit unit,
        int minStock,
        String location
) {

    public SparePartDetails {
        if (sku == null || sku.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("A part needs a SKU and a name");
        }
        if (category == null) throw new IllegalArgumentException("Part category is required");
        if (unit == null) throw new IllegalArgumentException("Part unit is required");
        if (minStock < 0) throw new IllegalArgumentException("Quantities are whole, non-negative numbers");
        sku = sku.trim().toUpperCase();
        name = name.trim();
        location = location == null ? "" : location.trim();
    }
}
