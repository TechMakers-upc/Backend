package com.techmakers.fixcore.assetmanagement.interfaces.acl;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllPlantsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetsByPlantIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetPlantByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetQueryService;
import com.techmakers.fixcore.assetmanagement.domain.services.PlantQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssetsContextFacade {

    private final AssetQueryService assetQueryService;
    private final PlantQueryService plantQueryService;

    public AssetsContextFacade(AssetQueryService assetQueryService, PlantQueryService plantQueryService) {
        this.assetQueryService = assetQueryService;
        this.plantQueryService = plantQueryService;
    }

    public Optional<Long> fetchPlantIdOfAsset(Long assetId) {
        return assetQueryService.handle(new GetAssetByIdQuery(assetId)).map(Asset::getPlantId);
    }

    public boolean existsPlant(Long plantId) {
        return plantQueryService.handle(new GetPlantByIdQuery(plantId)).isPresent();
    }

    public Optional<Long> fetchPlantIdByName(String plantName) {
        return plantQueryService.handle(new GetAllPlantsQuery()).stream()
                .filter(plant -> plant.getName().equalsIgnoreCase(plantName.trim()))
                .map(Plant::getId)
                .findFirst();
    }

    public Optional<Long> fetchAssetIdByPlantAndCode(Long plantId, String assetCode) {
        return assetQueryService.handle(new GetAssetsByPlantIdQuery(plantId)).stream()
                .filter(asset -> asset.getDetails().code().equalsIgnoreCase(assetCode.trim()))
                .map(Asset::getId)
                .findFirst();
    }
}
