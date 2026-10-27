package com.techmakers.fixcore.iam.interfaces.rest.transform;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.valueobjects.AuthenticatedUser;
import com.techmakers.fixcore.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.techmakers.fixcore.iam.interfaces.rest.resources.UserResource;

public final class UserResourceAssembler {

    private UserResourceAssembler() {
    }

    public static UserResource toResource(User user) {
        return new UserResource(user.getId(), user.getFullName().value(), user.getEmail().address(),
                RoleAssembler.toValue(user.getRole()), user.getPlantId(), user.getJobTitle());
    }

    public static AuthenticatedUserResource toResource(AuthenticatedUser authenticatedUser) {
        return new AuthenticatedUserResource(toResource(authenticatedUser.user()), authenticatedUser.token());
    }
}
