package com.techmakers.fixcore.assetmanagement.application.internal.queryservices;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllPlantsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetPlantByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.repositories.PlantRepository;
import com.techmakers.fixcore.assetmanagement.domain.services.PlantQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PlantQueryServiceImpl implements PlantQueryService {

    private final PlantRepository plantRepository;

    public PlantQueryServiceImpl(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @Override
    public List<Plant> handle(GetAllPlantsQuery query) {
        return plantRepository.findAll();
    }

    @Override
    public Optional<Plant> handle(GetPlantByIdQuery query) {
        return plantRepository.findById(query.plantId());
    }
}
