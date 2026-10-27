package com.techmakers.fixcore.serviceexecution.domain.model.valueobjects;

import java.time.Instant;

public record WorkLogEntry(Instant at, Long authorId, String note) {

    public WorkLogEntry {
        if (at == null) throw new IllegalArgumentException("Log date is required");
        if (note == null || note.isBlank()) throw new IllegalArgumentException("Empty note");
        note = note.trim();
    }
}
