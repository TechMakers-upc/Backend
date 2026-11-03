package com.techmakers.fixcore.notifications.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AlertReadReceiptId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "alert_id", nullable = false, length = 160)
    private String alertId;

    protected AlertReadReceiptId() {
    }

    public AlertReadReceiptId(Long userId, String alertId) {
        this.userId = Objects.requireNonNull(userId);
        this.alertId = Objects.requireNonNull(alertId);
    }

    public Long getUserId() {
        return userId;
    }

    public String getAlertId() {
        return alertId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AlertReadReceiptId other)) return false;
        return Objects.equals(userId, other.userId)
                && Objects.equals(alertId, other.alertId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, alertId);
    }
}
