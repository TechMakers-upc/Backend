package com.techmakers.fixcore.inventory.application.internal.queryservices;

import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.domain.model.queries.GetStockMovementsQuery;
import com.techmakers.fixcore.inventory.domain.repositories.StockMovementRepository;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.inventory.domain.services.StockMovementQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockMovementQueryServiceImpl implements StockMovementQueryService {

    private final StockMovementRepository movementRepository;
    private final PlantAccessGuard plantAccessGuard;

    public StockMovementQueryServiceImpl(StockMovementRepository movementRepository,
                                         PlantAccessGuard plantAccessGuard) {
        this.movementRepository = movementRepository;
        this.plantAccessGuard = plantAccessGuard;
    }

    @Override
    public List<StockMovement> handle(GetStockMovementsQuery query) {
        if (query.plantId() != null) {
            plantAccessGuard.ensureAccessTo(query.plantId());
        }

        List<StockMovement> movements;

        if (query.partId() != null) movements = movementRepository.findAllByPartId(query.partId());
        else if (query.plantId() != null) movements = movementRepository.findAllByPlantId(query.plantId());
        else movements = movementRepository.findAll();

        return movements.stream()
                .filter(movement -> canRead(movement.plantId()))
                .filter(movement -> query.plantId() == null || query.plantId().equals(movement.plantId()))
                .toList();
    }

    private boolean canRead(Long plantId) {
        return plantAccessGuard.restrictedPlantId()
                .map(restricted -> restricted.equals(plantId))
                .orElse(true);
    }
}
