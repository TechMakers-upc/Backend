package com.techmakers.fixcore.assetmanagement.domain.model.commands;

public record DeletePlantCommand(Long plantId) {

    public DeletePlantCommand {
        if (plantId == null || plantId <= 0) throw new IllegalArgumentException("Plant identifier must be a positive number");
    }
}
