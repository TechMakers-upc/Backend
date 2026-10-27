package com.techmakers.fixcore.iam.domain.model.valueobjects;

import java.util.regex.Pattern;

public record EmailAddress(String address) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address is required");
        }
        address = address.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("Email address is not valid");
        }
    }
}
