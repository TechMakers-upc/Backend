package com.techmakers.fixcore.serviceexecution.infrastructure.acl;

import com.techmakers.fixcore.inventory.interfaces.acl.InventoryContextFacade;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.StockConsumer;
import org.springframework.stereotype.Component;

@Component
public class StockConsumerAdapter implements StockConsumer {

    private final InventoryContextFacade inventoryContextFacade;

    public StockConsumerAdapter(InventoryContextFacade inventoryContextFacade) {
        this.inventoryContextFacade = inventoryContextFacade;
    }

    @Override
    public void consume(Long partId, int quantity, Long workOrderId, String reason, String performedBy) {
        inventoryContextFacade.consumeStock(partId, quantity, workOrderId, reason, performedBy);
    }
}
