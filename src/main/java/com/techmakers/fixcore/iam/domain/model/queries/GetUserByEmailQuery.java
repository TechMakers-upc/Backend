package com.techmakers.fixcore.iam.domain.model.queries;

import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;

public record GetUserByEmailQuery(EmailAddress email) {

    public GetUserByEmailQuery {
        if (email == null) throw new IllegalArgumentException("Email is required");
    }
}
