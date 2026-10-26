package com.techmakers.fixcore.serviceplanning.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RescheduleMaintenancePlanResource(@NotNull LocalDate nextDueDate) {
}
