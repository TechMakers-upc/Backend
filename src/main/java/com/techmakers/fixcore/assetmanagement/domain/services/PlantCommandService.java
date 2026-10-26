package com.techmakers.fixcore.assetmanagement.domain.services;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.CreatePlantCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.DeletePlantCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.UpdatePlantCommand;

public interface PlantCommandService {

    Plant handle(CreatePlantCommand command);

    Plant handle(UpdatePlantCommand command);

    void handle(DeletePlantCommand command);
}
