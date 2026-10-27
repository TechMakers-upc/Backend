package com.techmakers.fixcore.serviceexecution.domain.model.valueobjects;

import java.time.LocalDate;

public record WorkOrderDetails(String title, String description, Priority priority, LocalDate dueDate) {

    public WorkOrderDetails {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("A work order needs a title");
        if (priority == null) throw new IllegalArgumentException("Priority is required");
        if (dueDate == null) throw new IllegalArgumentException("Due date is required");
        title = title.trim();
        description = description == null ? "" : description.trim();
    }
}
