package com.techmakers.fixcore.iam.domain.model.commands;

import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;

public record SignInCommand(EmailAddress email, String password) {

    public SignInCommand {
        if (email == null) throw new IllegalArgumentException("Email is required");
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
