package com.techmakers.fixcore.assetmanagement.domain.model.commands;

public record UpdatePlantCommand(Long plantId, String name, String city, String address) {

    public UpdatePlantCommand {
        if (plantId == null || plantId <= 0) throw new IllegalArgumentException("Plant identifier must be a positive number");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Plant name is required");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("Plant city is required");
    }
}
