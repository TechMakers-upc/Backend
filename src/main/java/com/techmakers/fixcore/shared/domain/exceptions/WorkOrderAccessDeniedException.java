package com.techmakers.fixcore.shared.domain.exceptions;

public class WorkOrderAccessDeniedException extends RuntimeException {

    public WorkOrderAccessDeniedException() {
        super("You do not have permission to access this work order");
    }
}
