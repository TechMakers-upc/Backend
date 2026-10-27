package com.techmakers.fixcore.serviceexecution.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record PrioritizeFailureResource(@NotBlank String priority) {
}
