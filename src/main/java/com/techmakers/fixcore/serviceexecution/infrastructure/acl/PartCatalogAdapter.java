package com.techmakers.fixcore.serviceexecution.infrastructure.acl;

import com.techmakers.fixcore.inventory.interfaces.acl.InventoryContextFacade;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.PartCatalog;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PartCatalogAdapter implements PartCatalog {

    private final InventoryContextFacade inventoryContextFacade;

    public PartCatalogAdapter(InventoryContextFacade inventoryContextFacade) {
        this.inventoryContextFacade = inventoryContextFacade;
    }

    @Override
    public Optional<Long> plantIdOfPart(Long partId) {
        return inventoryContextFacade.fetchPlantIdOfPart(partId);
    }

    @Override
    public Optional<Long> partIdByPlantAndSku(Long plantId, String sku) {
        return inventoryContextFacade.fetchPartIdByPlantAndSku(plantId, sku);
    }
}
