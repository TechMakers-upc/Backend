package com.techmakers.fixcore.notifications.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "alert_read_receipt")
public class AlertReadReceiptEntity {

    @EmbeddedId
    private AlertReadReceiptId id;

    @Column(name = "read_at", nullable = false)
    private Instant readAt;

    protected AlertReadReceiptEntity() {
    }

    public AlertReadReceiptEntity(AlertReadReceiptId id, Instant readAt) {
        this.id = id;
        this.readAt = readAt;
    }

    public AlertReadReceiptId getId() {
        return id;
    }

    public Instant getReadAt() {
        return readAt;
    }
}
