package com.techmakers.fixcore.iam.infrastructure.acl;

import com.techmakers.fixcore.assetmanagement.interfaces.acl.AssetsContextFacade;
import com.techmakers.fixcore.iam.application.outboundservices.acl.PlantDirectory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlantDirectoryAdapter implements PlantDirectory {

    private final AssetsContextFacade assetsContextFacade;

    public PlantDirectoryAdapter(AssetsContextFacade assetsContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
    }

    @Override
    public boolean exists(Long plantId) {
        return assetsContextFacade.existsPlant(plantId);
    }

    @Override
    public Optional<Long> plantIdByName(String plantName) {
        return assetsContextFacade.fetchPlantIdByName(plantName);
    }
}
