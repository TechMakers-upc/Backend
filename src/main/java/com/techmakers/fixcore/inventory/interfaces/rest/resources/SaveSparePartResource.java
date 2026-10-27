package com.techmakers.fixcore.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveSparePartResource(
        Long plantId,
        @NotBlank @Size(max = 40) String sku,
        @NotBlank @Size(max = 150) String name,
        @NotBlank String category,
        @NotBlank String unit,
        @Min(0) int minStock,
        @Size(max = 60) String location
) {
}
