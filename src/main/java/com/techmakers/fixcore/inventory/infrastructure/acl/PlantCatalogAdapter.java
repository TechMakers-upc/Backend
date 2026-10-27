package com.techmakers.fixcore.inventory.infrastructure.acl;

import com.techmakers.fixcore.assetmanagement.interfaces.acl.AssetsContextFacade;
import com.techmakers.fixcore.inventory.application.outboundservices.acl.PlantCatalog;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlantCatalogAdapter implements PlantCatalog {

    private final AssetsContextFacade assetsContextFacade;

    public PlantCatalogAdapter(AssetsContextFacade assetsContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
    }

    @Override
    public boolean existsPlant(Long plantId) {
        return assetsContextFacade.existsPlant(plantId);
    }

    @Override
    public Optional<Long> plantIdByName(String plantName) {
        return assetsContextFacade.fetchPlantIdByName(plantName);
    }
}
