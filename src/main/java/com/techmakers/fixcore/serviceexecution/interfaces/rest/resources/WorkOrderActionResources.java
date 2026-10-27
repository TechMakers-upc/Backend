package com.techmakers.fixcore.serviceexecution.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class WorkOrderActionResources {

    private WorkOrderActionResources() {
    }

    public record AssignTechnicianResource(@NotNull Long technicianId) {
    }

    public record LogWorkResource(@NotBlank @Size(max = 1000) String note) {
    }

    public record RegisterPartUsageResource(@NotNull Long partId, @Min(1) int quantity) {
    }

    public record CompleteWorkOrderResource(@NotBlank @Size(max = 2000) String solution) {
    }
}
