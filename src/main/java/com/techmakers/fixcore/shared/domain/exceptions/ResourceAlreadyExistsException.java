package com.techmakers.fixcore.shared.domain.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resourceName, String field, Object value) {
        super("%s with %s %s already exists".formatted(resourceName, field, value));
    }
}
