package com.techmakers.fixcore.assetmanagement.application.internal.queryservices;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllAssetsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetsByPlantIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.repositories.AssetRepository;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AssetQueryServiceImpl implements AssetQueryService {

    private final AssetRepository assetRepository;
    private final PlantAccessGuard plantAccessGuard;

    public AssetQueryServiceImpl(AssetRepository assetRepository, PlantAccessGuard plantAccessGuard) {
        this.assetRepository = assetRepository;
        this.plantAccessGuard = plantAccessGuard;
    }

    @Override
    public List<Asset> handle(GetAllAssetsQuery query) {
        return assetRepository.findAll().stream().filter(asset -> canRead(asset.getPlantId())).toList();
    }

    @Override
    public List<Asset> handle(GetAssetsByPlantIdQuery query) {
        plantAccessGuard.ensureAccessTo(query.plantId());
        return assetRepository.findAllByPlantId(query.plantId());
    }

    @Override
    public Optional<Asset> handle(GetAssetByIdQuery query) {
        return assetRepository.findById(query.assetId())
                .map(asset -> {
                    plantAccessGuard.ensureAccessTo(asset.getPlantId());
                    return asset;
                });
    }

    private boolean canRead(Long plantId) {
        return plantAccessGuard.restrictedPlantId()
                .map(restricted -> restricted.equals(plantId))
                .orElse(true);
    }
}
