package com.techmakers.fixcore.assetmanagement.domain.model.commands;

import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetDetails;

public record CreateAssetCommand(Long plantId, AssetDetails details) {

    public CreateAssetCommand {
        if (plantId == null || plantId <= 0) throw new IllegalArgumentException("Plant identifier must be a positive number");
        if (details == null) throw new IllegalArgumentException("Asset details are required");
    }
}
