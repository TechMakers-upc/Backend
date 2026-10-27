package com.techmakers.fixcore.iam.domain.model.commands;

import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.FullName;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;

public record SignUpCommand(
        EmailAddress email,
        String password,
        FullName fullName,
        String jobTitle,
        Long plantId,
        Roles role
) {

    public SignUpCommand {
        if (email == null) throw new IllegalArgumentException("Email is required");
        if (fullName == null) throw new IllegalArgumentException("Full name is required");
        if (role == null) throw new IllegalArgumentException("Role is required");
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must have at least 8 characters");
        }
    }
}
