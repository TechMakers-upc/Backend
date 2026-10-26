package com.techmakers.fixcore.assetmanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SavePlantResource(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 80) String city,
        @Size(max = 200) String address
) {
}
