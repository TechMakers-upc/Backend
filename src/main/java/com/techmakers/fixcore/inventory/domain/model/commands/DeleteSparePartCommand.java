package com.techmakers.fixcore.inventory.domain.model.commands;

public record DeleteSparePartCommand(Long partId) {

    public DeleteSparePartCommand {
        if (partId == null || partId <= 0) throw new IllegalArgumentException("Part identifier must be a positive number");
    }
}
