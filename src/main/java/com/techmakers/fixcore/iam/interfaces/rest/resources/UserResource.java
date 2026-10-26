package com.techmakers.fixcore.iam.interfaces.rest.resources;

public record UserResource(
        Long id,
        String fullName,
        String email,
        String role,
        Long plantId,
        String jobTitle
) {
}
