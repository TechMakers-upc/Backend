package com.techmakers.fixcore.assetmanagement.domain.model.commands;

import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetStatus;

public record ChangeAssetStatusCommand(Long assetId, AssetStatus status) {

    public ChangeAssetStatusCommand {
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
        if (status == null) throw new IllegalArgumentException("Asset status is required");
    }
}
