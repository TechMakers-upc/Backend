package com.techmakers.fixcore.shared.domain.exceptions;

public class PlantAccessDeniedException extends RuntimeException {

    public PlantAccessDeniedException(Long plantId) {
        super("You do not have access to plant " + plantId);
    }
}
