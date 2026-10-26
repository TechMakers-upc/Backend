package com.techmakers.fixcore.shared.domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("%s with identifier %s was not found".formatted(resourceName, identifier));
    }
}
