package com.techmakers.fixcore.inventory.interfaces.acl;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.commands.ConsumeStockCommand;
import com.techmakers.fixcore.inventory.domain.services.StockCommandService;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartByIdQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartsByPlantIdQuery;
import com.techmakers.fixcore.inventory.domain.services.SparePartQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryContextFacade {

    private final SparePartQueryService partQueryService;
    private final StockCommandService stockCommandService;

    public InventoryContextFacade(SparePartQueryService partQueryService, StockCommandService stockCommandService) {
        this.partQueryService = partQueryService;
        this.stockCommandService = stockCommandService;
    }

    public void consumeStock(Long partId, int quantity, Long workOrderId, String reason, String performedBy) {
        stockCommandService.handle(new ConsumeStockCommand(partId, quantity, workOrderId, reason, performedBy));
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
