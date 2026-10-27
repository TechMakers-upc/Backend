package com.techmakers.fixcore.serviceplanning.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SaveMaintenancePlanResource(
        @NotNull Long assetId,
        @NotBlank @Size(max = 150) String title,
        @Size(max = 2000) String tasks,
        @Min(1) @Max(730) int frequencyDays,
        @NotNull LocalDate nextDueDate,
        Long technicianId
) {
}
