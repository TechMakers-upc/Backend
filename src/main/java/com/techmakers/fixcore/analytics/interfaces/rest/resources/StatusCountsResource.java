package com.techmakers.fixcore.analytics.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StatusCountsResource(long pending, @JsonProperty("in-progress") long inProgress, long completed) {
}
