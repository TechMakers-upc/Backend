package com.techmakers.fixcore.assetmanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddManualResource(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 500) String url
) {
}
