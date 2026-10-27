package com.techmakers.fixcore.notifications.domain.model.valueobjects;

public record ScarcePart(Long id, String name, int stock, int minStock, boolean out) {
}
