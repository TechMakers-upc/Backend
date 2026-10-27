package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.entities.StockMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementEntity, Long> {

    List<StockMovementEntity> findAllByOrderByOccurredAtDesc();

    List<StockMovementEntity> findAllByPlantIdOrderByOccurredAtDesc(Long plantId);

    List<StockMovementEntity> findAllByPartIdOrderByOccurredAtDesc(Long partId);

    boolean existsByPartId(Long partId);
}
