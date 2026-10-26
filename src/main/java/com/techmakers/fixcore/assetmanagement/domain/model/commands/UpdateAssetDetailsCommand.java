package com.techmakers.fixcore.assetmanagement.domain.model.commands;

import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetDetails;

public record UpdateAssetDetailsCommand(Long assetId, AssetDetails details) {

    public UpdateAssetDetailsCommand {
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
        if (details == null) throw new IllegalArgumentException("Asset details are required");
    }
}
