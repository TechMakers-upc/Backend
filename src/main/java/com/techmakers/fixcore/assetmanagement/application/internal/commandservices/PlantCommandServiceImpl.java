package com.techmakers.fixcore.assetmanagement.application.internal.commandservices;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.CreatePlantCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.DeletePlantCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.UpdatePlantCommand;
import com.techmakers.fixcore.assetmanagement.domain.repositories.AssetRepository;
import com.techmakers.fixcore.assetmanagement.domain.repositories.PlantRepository;
import com.techmakers.fixcore.assetmanagement.domain.services.PlantCommandService;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceAlreadyExistsException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlantCommandServiceImpl implements PlantCommandService {

    private final PlantRepository plantRepository;
    private final AssetRepository assetRepository;

    public PlantCommandServiceImpl(PlantRepository plantRepository, AssetRepository assetRepository) {
        this.plantRepository = plantRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public Plant handle(CreatePlantCommand command) {
        if (plantRepository.existsByName(command.name())) {
            throw new ResourceAlreadyExistsException("Plant", "name", command.name().trim());
        }
        return plantRepository.save(new Plant(command.name(), command.city(), command.address()));
    }

    @Override
    public Plant handle(UpdatePlantCommand command) {
        var plant = plantRepository.findById(command.plantId())
                .orElseThrow(() -> new ResourceNotFoundException("Plant", command.plantId()));
        if (plantRepository.existsByNameAndIdNot(command.name(), command.plantId())) {
            throw new ResourceAlreadyExistsException("Plant", "name", command.name().trim());
        }
        plant.updateDetails(command.name(), command.city(), command.address());
        return plantRepository.save(plant);
    }

    @Override
    public void handle(DeletePlantCommand command) {
        if (!plantRepository.existsById(command.plantId())) {
            throw new ResourceNotFoundException("Plant", command.plantId());
        }
        if (assetRepository.existsByPlantId(command.plantId())) {
            throw new BusinessRuleViolationException("A plant with registered assets cannot be deleted");
        }
        plantRepository.deleteById(command.plantId());
    }
}
