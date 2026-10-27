package com.techmakers.fixcore.inventory.interfaces.rest.resources;

public record SparePartResource(
        Long id,
        Long plantId,
        String sku,
        String name,
        String category,
        String unit,
        int stock,
        int minStock,
        String location,
        String level
) {
}
