package com.techmakers.fixcore.shared.domain.exceptions;

public class MaintenancePlanAccessDeniedException extends RuntimeException {

    public MaintenancePlanAccessDeniedException() {
        super("You do not have permission to complete this maintenance plan");
    }
}
