package com.techmakers.fixcore.assetmanagement.domain.model.commands;

public record AddAssetManualCommand(Long assetId, String title, String url) {

    public AddAssetManualCommand {
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
    }
}
