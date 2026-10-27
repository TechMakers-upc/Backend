package com.techmakers.fixcore.notifications.domain.model.valueobjects;

public record Recipient(Long userId, RecipientRole role, Long plantId) {

    public boolean isTechnician() {
        return role == RecipientRole.TECHNICIAN;
    }
}
