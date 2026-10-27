package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.entities.SparePartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SparePartJpaRepository extends JpaRepository<SparePartEntity, Long> {

    List<SparePartEntity> findAllByOrderByNameAsc();

    List<SparePartEntity> findAllByPlantIdOrderByNameAsc(Long plantId);

    boolean existsByPlantIdAndSku(Long plantId, String sku);

    boolean existsByPlantIdAndSkuAndIdNot(Long plantId, String sku, Long id);
}
