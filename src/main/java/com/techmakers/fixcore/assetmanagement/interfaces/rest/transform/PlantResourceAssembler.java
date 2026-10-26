package com.techmakers.fixcore.assetmanagement.interfaces.rest.transform;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.CreatePlantCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.UpdatePlantCommand;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.PlantResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.SavePlantResource;

public final class PlantResourceAssembler {

    private PlantResourceAssembler() {
    }

    public static PlantResource toResource(Plant plant) {
        return new PlantResource(plant.getId(), plant.getName(), plant.getCity(), plant.getAddress(), plant.displayName());
    }

    public static CreatePlantCommand toCreateCommand(SavePlantResource resource) {
        return new CreatePlantCommand(resource.name(), resource.city(), resource.address());
    }

    public static UpdatePlantCommand toUpdateCommand(Long plantId, SavePlantResource resource) {
        return new UpdatePlantCommand(plantId, resource.name(), resource.city(), resource.address());
    }
}
