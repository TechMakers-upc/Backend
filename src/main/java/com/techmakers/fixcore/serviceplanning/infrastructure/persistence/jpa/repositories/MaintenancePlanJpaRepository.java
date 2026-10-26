package com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.entities.MaintenancePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenancePlanJpaRepository extends JpaRepository<MaintenancePlanEntity, Long> {

    List<MaintenancePlanEntity> findAllByOrderByNextDueDateAsc();

    List<MaintenancePlanEntity> findAllByPlantIdOrderByNextDueDateAsc(Long plantId);

    List<MaintenancePlanEntity> findAllByAssetIdOrderByNextDueDateAsc(Long assetId);

    boolean existsByAssetIdAndTitleIgnoreCase(Long assetId, String title);
}
