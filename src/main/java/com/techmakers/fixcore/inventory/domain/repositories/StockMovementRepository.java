package com.techmakers.fixcore.inventory.domain.repositories;

import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;

import java.util.List;

public interface StockMovementRepository {

    StockMovement save(StockMovement movement);

    List<StockMovement> findAll();

    List<StockMovement> findAllByPlantId(Long plantId);

    List<StockMovement> findAllByPartId(Long partId);

    boolean existsByPartId(Long partId);
}
