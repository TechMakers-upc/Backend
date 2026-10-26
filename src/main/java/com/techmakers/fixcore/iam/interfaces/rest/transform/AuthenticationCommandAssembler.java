package com.techmakers.fixcore.iam.interfaces.rest.transform;

import com.techmakers.fixcore.iam.domain.model.commands.SignInCommand;
import com.techmakers.fixcore.iam.domain.model.commands.SignUpCommand;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.FullName;
import com.techmakers.fixcore.iam.interfaces.rest.resources.SignInResource;
import com.techmakers.fixcore.iam.interfaces.rest.resources.SignUpResource;

public final class AuthenticationCommandAssembler {

    private AuthenticationCommandAssembler() {
    }

    public static SignUpCommand toCommand(SignUpResource resource) {
        return new SignUpCommand(new EmailAddress(resource.email()), resource.password(),
                new FullName(resource.fullName()), resource.jobTitle(), resource.plantId(),
                RoleAssembler.toRole(resource.role()));
    }

    public static SignInCommand toCommand(SignInResource resource) {
        return new SignInCommand(new EmailAddress(resource.email()), resource.password());
    }
}
