package com.techmakers.fixcore.assetmanagement.interfaces.rest.resources;

import java.time.Instant;

public record ManualResource(String title, String url, Instant addedAt) {
}
