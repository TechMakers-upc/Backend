package com.techmakers.fixcore.inventory.domain.repositories;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;

import java.util.List;
import java.util.Optional;

public interface SparePartRepository {

    SparePart save(SparePart part);

    Optional<SparePart> findById(Long id);

    List<SparePart> findAll();

    List<SparePart> findAllByPlantId(Long plantId);

    boolean existsById(Long id);

    boolean existsByPlantIdAndSku(Long plantId, String sku);

    boolean existsByPlantIdAndSkuAndIdNot(Long plantId, String sku, Long id);

    void deleteById(Long id);
}
