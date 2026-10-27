package com.techmakers.fixcore.inventory.domain.model.commands;

import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;

public record UpdateSparePartDetailsCommand(Long partId, SparePartDetails details) {

    public UpdateSparePartDetailsCommand {
        if (partId == null || partId <= 0) throw new IllegalArgumentException("Part identifier must be a positive number");
        if (details == null) throw new IllegalArgumentException("Part details are required");
    }
}
