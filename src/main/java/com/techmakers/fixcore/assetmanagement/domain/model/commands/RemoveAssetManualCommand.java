package com.techmakers.fixcore.assetmanagement.domain.model.commands;

public record RemoveAssetManualCommand(Long assetId, int manualIndex) {

    public RemoveAssetManualCommand {
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
        if (manualIndex < 0) throw new IllegalArgumentException("Manual index cannot be negative");
    }
}
