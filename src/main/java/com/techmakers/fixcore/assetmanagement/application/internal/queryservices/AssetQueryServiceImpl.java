package com.techmakers.fixcore.assetmanagement.application.internal.queryservices;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllAssetsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetsByPlantIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.repositories.AssetRepository;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AssetQueryServiceImpl implements AssetQueryService {

    private final AssetRepository assetRepository;

    public AssetQueryServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public List<Asset> handle(GetAllAssetsQuery query) {
        return assetRepository.findAll();
    }

    @Override
    public List<Asset> handle(GetAssetsByPlantIdQuery query) {
        return assetRepository.findAllByPlantId(query.plantId());
    }

    @Override
    public Optional<Asset> handle(GetAssetByIdQuery query) {
        return assetRepository.findById(query.assetId());
    }
}
