package com.techmakers.fixcore.iam.interfaces.rest.transform;

import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.interfaces.rest.resources.RoleResource;

import java.util.Arrays;

public final class RoleAssembler {

    private RoleAssembler() {
    }

    public static Roles toRole(String value) {
        return Arrays.stream(Roles.values())
                .filter(role -> toValue(role).equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Role %s is not supported".formatted(value)));
    }

    public static String toValue(Roles role) {
        return role.name().toLowerCase().replace('_', '-');
    }

    public static RoleResource toResource(Roles role) {
        return new RoleResource(toValue(role), role.requiresPlant());
    }
}
