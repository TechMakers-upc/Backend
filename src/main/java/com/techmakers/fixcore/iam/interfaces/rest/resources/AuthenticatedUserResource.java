package com.techmakers.fixcore.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(UserResource account, String token) {
}
