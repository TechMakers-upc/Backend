package com.techmakers.fixcore.inventory.application.internal.queryservices;

import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.domain.model.queries.GetStockMovementsQuery;
import com.techmakers.fixcore.inventory.domain.repositories.StockMovementRepository;
import com.techmakers.fixcore.inventory.domain.services.StockMovementQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockMovementQueryServiceImpl implements StockMovementQueryService {

    private final StockMovementRepository movementRepository;

    public StockMovementQueryServiceImpl(StockMovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    @Override
    public List<StockMovement> handle(GetStockMovementsQuery query) {
        if (query.partId() != null) return movementRepository.findAllByPartId(query.partId());
        if (query.plantId() != null) return movementRepository.findAllByPlantId(query.plantId());
        return movementRepository.findAll();
    }
}
