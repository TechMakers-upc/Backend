package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

@Embeddable
public class WorkLogEntryEmbeddable {

    @Column(nullable = false)
    private Instant occurredAt;

    private Long authorId;

    @Column(nullable = false, length = 1000)
    private String note;

    protected WorkLogEntryEmbeddable() {
    }

    public WorkLogEntryEmbeddable(Instant occurredAt, Long authorId, String note) {
        this.occurredAt = occurredAt;
        this.authorId = authorId;
        this.note = note;
    }

    public Instant getOccurredAt() { return occurredAt; }

    public Long getAuthorId() { return authorId; }

    public String getNote() { return note; }
}
