package com.techmakers.fixcore.inventory.domain.services;

import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.domain.model.queries.GetStockMovementsQuery;

import java.util.List;

public interface StockMovementQueryService {

    List<StockMovement> handle(GetStockMovementsQuery query);
}
