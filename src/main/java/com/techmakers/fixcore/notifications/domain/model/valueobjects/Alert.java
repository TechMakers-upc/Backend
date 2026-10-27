package com.techmakers.fixcore.notifications.domain.model.valueobjects;

import java.time.Instant;
import java.util.Map;

public record Alert(String id, AlertKind kind, AlertSeverity severity, Map<String, Object> params, String link,
                    Instant at) {
}
