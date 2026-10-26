package com.techmakers.fixcore.assetmanagement.domain.model.queries;

public record GetAssetByIdQuery(Long assetId) {

    public GetAssetByIdQuery {
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
    }
}
