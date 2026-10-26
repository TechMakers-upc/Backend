package com.techmakers.fixcore.assetmanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SaveAssetResource(
        Long plantId,
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 150) String name,
        @NotBlank String category,
        @NotBlank String criticality,
        @Size(max = 120) String location,
        @Size(max = 80) String brand,
        @Size(max = 80) String model,
        @Size(max = 80) String serialNumber,
        LocalDate installedAt,
        @Size(max = 1000) String description
) {
}
