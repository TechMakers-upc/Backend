package com.techmakers.fixcore.assetmanagement.domain.services;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllPlantsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetPlantByIdQuery;

import java.util.List;
import java.util.Optional;

public interface PlantQueryService {

    List<Plant> handle(GetAllPlantsQuery query);

    Optional<Plant> handle(GetPlantByIdQuery query);
}
