package com.techmakers.fixcore.inventory.interfaces.acl;

public record PartSummary(Long id, Long plantId, String name, int stock, int minStock, String level) {
}
