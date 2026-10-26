package com.techmakers.fixcore.assetmanagement.interfaces.rest.resources;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record AssetResource(
        Long id,
        Long plantId,
        String code,
        String name,
        String category,
        String criticality,
        String status,
        Instant statusChangedAt,
        String location,
        String brand,
        String model,
        String serialNumber,
        LocalDate installedAt,
        String description,
        List<ManualResource> manuals
) {
}
