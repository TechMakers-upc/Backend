package com.techmakers.fixcore.serviceexecution.domain.model.commands;

public record LogWorkCommand(Long workOrderId, String note, String author) {

    public LogWorkCommand {
        if (workOrderId == null || workOrderId <= 0) throw new IllegalArgumentException("Work order identifier must be a positive number");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("The author is required");
    }
}
