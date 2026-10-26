package com.techmakers.fixcore.assetmanagement.domain.model.valueobjects;

import java.time.LocalDate;

public record AssetDetails(
        String code,
        String name,
        AssetCategory category,
        Criticality criticality,
        String location,
        String brand,
        String model,
        String serialNumber,
        LocalDate installedAt,
        String description
) {

    public AssetDetails {
        if (code == null || code.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("An asset needs a code and a name");
        }
        if (category == null) throw new IllegalArgumentException("Asset category is required");
        if (criticality == null) throw new IllegalArgumentException("Asset criticality is required");
        code = code.trim().toUpperCase();
        name = name.trim();
        location = clean(location);
        brand = clean(brand);
        model = clean(model);
        serialNumber = clean(serialNumber);
        description = clean(description);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
