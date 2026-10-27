package com.techmakers.fixcore.assetmanagement.domain.model.valueobjects;

import java.time.Instant;

public record Manual(String title, String url, Instant addedAt) {

    public Manual {
        if (title == null || title.isBlank() || url == null || !url.trim().matches("(?i)^https?://.+")) {
            throw new IllegalArgumentException("A manual needs a title and an http(s) link");
        }
        if (addedAt == null) throw new IllegalArgumentException("Manual date is required");
        title = title.trim();
        url = url.trim();
    }
}
