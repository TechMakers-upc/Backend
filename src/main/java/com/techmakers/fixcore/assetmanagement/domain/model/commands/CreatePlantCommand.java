package com.techmakers.fixcore.assetmanagement.domain.model.commands;

public record CreatePlantCommand(String name, String city, String address) {

    public CreatePlantCommand {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Plant name is required");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("Plant city is required");
    }
}
