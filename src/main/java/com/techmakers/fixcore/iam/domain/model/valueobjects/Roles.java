package com.techmakers.fixcore.iam.domain.model.valueobjects;

public enum Roles {
    PLANT_MANAGER,
    OPERATIONS_MANAGER,
    TECHNICIAN;

    public boolean requiresPlant() {
        return this != OPERATIONS_MANAGER;
    }
}
