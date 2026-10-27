package com.techmakers.fixcore.notifications.interfaces.rest.resources;

import java.time.Instant;
import java.util.Map;

public record AlertResource(String id, String kind, String severity, Map<String, Object> params, String link,
                            Instant at) {
}
