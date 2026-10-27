package com.techmakers.fixcore.assetmanagement.interfaces.acl;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.ChangeAssetStatusCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetStatus;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetCommandService;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllPlantsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetsByPlantIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetPlantByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetQueryService;
import com.techmakers.fixcore.assetmanagement.domain.services.PlantQueryService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class AssetsContextFacade {

    private final AssetQueryService assetQueryService;
    private final AssetCommandService assetCommandService;
    private final PlantQueryService plantQueryService;

    public AssetsContextFacade(AssetQueryService assetQueryService, AssetCommandService assetCommandService,
                               PlantQueryService plantQueryService) {
        this.assetQueryService = assetQueryService;
        this.assetCommandService = assetCommandService;
        this.plantQueryService = plantQueryService;
    }

    public void markAssetDown(Long assetId) {
        assetCommandService.handle(new ChangeAssetStatusCommand(assetId, AssetStatus.DOWN));
    }

    public void markAssetInMaintenanceIfOperational(Long assetId) {
        assetQueryService.handle(new GetAssetByIdQuery(assetId))
                .filter(asset -> asset.getStatus() == AssetStatus.OPERATIONAL)
                .ifPresent(asset -> assetCommandService.handle(new ChangeAssetStatusCommand(assetId, AssetStatus.MAINTENANCE)));
    }

    public void markAssetOperational(Long assetId) {
        assetCommandService.handle(new ChangeAssetStatusCommand(assetId, AssetStatus.OPERATIONAL));
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

    public List<Long> fetchAllPlantIds() {
        return plantQueryService.handle(new GetAllPlantsQuery()).stream().map(Plant::getId).toList();
    }

    public List<AssetSummary> fetchAssetsOfPlants(Collection<Long> plantIds) {
        return plantIds.stream()
                .flatMap(plantId -> assetQueryService.handle(new GetAssetsByPlantIdQuery(plantId)).stream())
                .map(asset -> new AssetSummary(asset.getId(), asset.getPlantId(), asset.getDetails().name(),
                        asset.getStatus().name()))
                .toList();
    }

    public Optional<Long> fetchAssetIdByPlantAndCode(Long plantId, String assetCode) {
        return assetQueryService.handle(new GetAssetsByPlantIdQuery(plantId)).stream()
                .filter(asset -> asset.getDetails().code().equalsIgnoreCase(assetCode.trim()))
                .map(Asset::getId)
                .findFirst();
    }
}
