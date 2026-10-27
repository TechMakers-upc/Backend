package com.techmakers.fixcore.inventory.interfaces.acl;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartByIdQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartsByPlantIdQuery;
import com.techmakers.fixcore.inventory.domain.services.SparePartQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryContextFacade {

    private final SparePartQueryService partQueryService;

    public InventoryContextFacade(SparePartQueryService partQueryService) {
        this.partQueryService = partQueryService;
    }

    public Optional<Long> fetchPlantIdOfPart(Long partId) {
        return partQueryService.handle(new GetSparePartByIdQuery(partId)).map(SparePart::getPlantId);
    }

    public Optional<Long> fetchPartIdByPlantAndSku(Long plantId, String sku) {
        return partQueryService.handle(new GetSparePartsByPlantIdQuery(plantId)).stream()
                .filter(part -> part.getDetails().sku().equalsIgnoreCase(sku.trim()))
                .map(SparePart::getId)
                .findFirst();
    }
}
