package com.techmakers.fixcore.iam.domain.model.queries;

public record GetUserByIdQuery(Long userId) {

    public GetUserByIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User identifier must be a positive number");
        }
    }
}
