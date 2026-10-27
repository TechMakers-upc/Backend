package com.techmakers.fixcore.assetmanagement.domain.repositories;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {

    Asset save(Asset asset);

    Optional<Asset> findById(Long id);

    List<Asset> findAll();

    List<Asset> findAllByPlantId(Long plantId);

    boolean existsById(Long id);

    boolean existsByPlantId(Long plantId);

    boolean existsByPlantIdAndCode(Long plantId, String code);

    boolean existsByPlantIdAndCodeAndIdNot(Long plantId, String code, Long id);

    void deleteById(Long id);
}
