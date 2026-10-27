package com.techmakers.fixcore.assetmanagement.application.internal.eventhandlers;

import com.techmakers.fixcore.assetmanagement.domain.model.commands.CreatePlantCommand;
import com.techmakers.fixcore.assetmanagement.domain.repositories.PlantRepository;
import com.techmakers.fixcore.assetmanagement.domain.services.PlantCommandService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "fixcore.seed.demo-plants.enabled", havingValue = "true")
public class DemoPlantsSeeder {

    private static final List<CreatePlantCommand> DEMO_PLANTS = List.of(
            new CreatePlantCommand("Planta Ate", "Lima", "Av. Nicolás Ayllón 2480"),
            new CreatePlantCommand("Planta Callao", "Callao", "Av. Argentina 3250"),
            new CreatePlantCommand("Planta Arequipa", "Arequipa", "Parque Industrial Mz. B Lt. 4"));

    private final PlantRepository plantRepository;
    private final PlantCommandService plantCommandService;

    public DemoPlantsSeeder(PlantRepository plantRepository, PlantCommandService plantCommandService) {
        this.plantRepository = plantRepository;
        this.plantCommandService = plantCommandService;
    }

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoPlants() {
        DEMO_PLANTS.stream()
                .filter(plant -> !plantRepository.existsByName(plant.name()))
                .forEach(plantCommandService::handle);
    }
}
