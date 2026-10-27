package com.techmakers.fixcore.serviceplanning.infrastructure.acl;

import com.techmakers.fixcore.assetmanagement.interfaces.acl.AssetsContextFacade;
import com.techmakers.fixcore.serviceplanning.application.outboundservices.acl.AssetCatalog;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AssetCatalogAdapter implements AssetCatalog {

    private final AssetsContextFacade assetsContextFacade;

    public AssetCatalogAdapter(AssetsContextFacade assetsContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
    }

    @Override
    public Optional<Long> plantIdOfAsset(Long assetId) {
        return assetsContextFacade.fetchPlantIdOfAsset(assetId);
    }

    @Override
    public Optional<Long> assetIdByPlantNameAndCode(String plantName, String assetCode) {
        return assetsContextFacade.fetchPlantIdByName(plantName)
                .flatMap(plantId -> assetsContextFacade.fetchAssetIdByPlantAndCode(plantId, assetCode));
    }
}
