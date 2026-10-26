package com.techmakers.fixcore.assetmanagement.domain.model.commands;

public record DeleteAssetCommand(Long assetId) {

    public DeleteAssetCommand {
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
    }
}
