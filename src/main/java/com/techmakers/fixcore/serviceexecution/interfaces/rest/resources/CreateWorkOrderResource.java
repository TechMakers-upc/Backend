package com.techmakers.fixcore.serviceexecution.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateWorkOrderResource(
        @NotBlank String type,
        @NotNull Long assetId,
        @NotBlank @Size(max = 150) String title,
        @Size(max = 2000) String description,
        @NotBlank String priority,
        @NotNull LocalDate dueDate,
        Long technicianId,
        Long failureId,
        Long planId
) {
}
