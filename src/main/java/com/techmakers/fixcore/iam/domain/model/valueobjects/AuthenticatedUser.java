package com.techmakers.fixcore.iam.domain.model.valueobjects;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;

public record AuthenticatedUser(User user, String token) {

    public AuthenticatedUser {
        if (user == null) throw new IllegalArgumentException("User is required");
        if (token == null || token.isBlank()) throw new IllegalArgumentException("Token is required");
    }
}
