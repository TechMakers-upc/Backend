package com.techmakers.fixcore.notifications.domain.model.queries;

public record GetAlertsQuery(String recipientEmail) {

    public GetAlertsQuery {
        if (recipientEmail == null || recipientEmail.isBlank()) throw new IllegalArgumentException("Recipient is required");
    }
}
