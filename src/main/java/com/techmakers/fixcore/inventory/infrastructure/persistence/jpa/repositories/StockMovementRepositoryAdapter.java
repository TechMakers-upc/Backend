package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.domain.repositories.StockMovementRepository;
import com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.entities.StockMovementEntity;
import com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.mappers.StockMovementPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockMovementRepositoryAdapter implements StockMovementRepository {

    private final StockMovementJpaRepository movementJpaRepository;

    public StockMovementRepositoryAdapter(StockMovementJpaRepository movementJpaRepository) {
        this.movementJpaRepository = movementJpaRepository;
    }

    @Override
    public StockMovement save(StockMovement movement) {
        var saved = movementJpaRepository.save(StockMovementPersistenceMapper.toEntity(movement));
        return StockMovementPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<StockMovement> findAll() {
        return toDomain(movementJpaRepository.findAllByOrderByOccurredAtDesc());
    }

    @Override
    public List<StockMovement> findAllByPlantId(Long plantId) {
        return toDomain(movementJpaRepository.findAllByPlantIdOrderByOccurredAtDesc(plantId));
    }

    @Override
    public List<StockMovement> findAllByPartId(Long partId) {
        return toDomain(movementJpaRepository.findAllByPartIdOrderByOccurredAtDesc(partId));
    }

    @Override
    public boolean existsByPartId(Long partId) {
        return movementJpaRepository.existsByPartId(partId);
    }

    private List<StockMovement> toDomain(List<StockMovementEntity> entities) {
        return entities.stream().map(StockMovementPersistenceMapper::toDomain).toList();
    }
}
