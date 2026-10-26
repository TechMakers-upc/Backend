package com.techmakers.fixcore.shared.domain.exceptions;

public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
