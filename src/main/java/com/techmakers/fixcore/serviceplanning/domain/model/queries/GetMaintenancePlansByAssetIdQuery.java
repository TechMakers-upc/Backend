package com.techmakers.fixcore.serviceplanning.domain.model.queries;

public record GetMaintenancePlansByAssetIdQuery(Long assetId) {

    public GetMaintenancePlansByAssetIdQuery {
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
    }
}
