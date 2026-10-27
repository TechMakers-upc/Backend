package com.techmakers.fixcore.serviceplanning.domain.repositories;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;

import java.util.List;
import java.util.Optional;

public interface MaintenancePlanRepository {

    MaintenancePlan save(MaintenancePlan plan);

    Optional<MaintenancePlan> findById(Long id);

    List<MaintenancePlan> findAll();

    List<MaintenancePlan> findAllByPlantId(Long plantId);

    List<MaintenancePlan> findAllByAssetId(Long assetId);

    boolean existsByAssetIdAndTitle(Long assetId, String title);

    boolean existsById(Long id);

    void deleteById(Long id);
}
