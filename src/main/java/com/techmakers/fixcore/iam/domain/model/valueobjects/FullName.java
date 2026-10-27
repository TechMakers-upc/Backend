package com.techmakers.fixcore.iam.domain.model.valueobjects;

public record FullName(String value) {

    public FullName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        value = value.trim().replaceAll("\\s+", " ");
    }

    public String firstName() {
        return value.split(" ")[0];
    }
}
