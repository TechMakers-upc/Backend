package com.techmakers.fixcore.assetmanagement.domain.services;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllAssetsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetsByPlantIdQuery;

import java.util.List;
import java.util.Optional;

public interface AssetQueryService {

    List<Asset> handle(GetAllAssetsQuery query);

    List<Asset> handle(GetAssetsByPlantIdQuery query);

    Optional<Asset> handle(GetAssetByIdQuery query);
}
