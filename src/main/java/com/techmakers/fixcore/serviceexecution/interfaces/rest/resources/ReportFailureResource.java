package com.techmakers.fixcore.serviceexecution.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportFailureResource(
        @NotNull Long assetId,
        @NotBlank String kind,
        @Size(max = 1000) String description,
        boolean lineStopped
) {
}
