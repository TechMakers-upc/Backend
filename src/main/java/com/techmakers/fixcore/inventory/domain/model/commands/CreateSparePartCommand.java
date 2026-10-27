package com.techmakers.fixcore.inventory.domain.model.commands;

import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;

public record CreateSparePartCommand(Long plantId, SparePartDetails details) {

    public CreateSparePartCommand {
        if (plantId == null || plantId <= 0) throw new IllegalArgumentException("Plant identifier must be a positive number");
        if (details == null) throw new IllegalArgumentException("Part details are required");
    }
}
