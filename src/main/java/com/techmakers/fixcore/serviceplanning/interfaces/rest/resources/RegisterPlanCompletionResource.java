package com.techmakers.fixcore.serviceplanning.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegisterPlanCompletionResource(@NotNull LocalDate completedOn) {
}
