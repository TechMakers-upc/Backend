package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderStatus;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderEntity, Long> {

    List<WorkOrderEntity> findAllByOrderByDueDateAsc();

    List<WorkOrderEntity> findAllByPlantIdOrderByDueDateAsc(Long plantId);

    List<WorkOrderEntity> findAllByCodeStartingWith(String prefix);

    boolean existsByCode(String code);

    boolean existsByAssetIdAndStatusAndIdNot(Long assetId, WorkOrderStatus status, Long id);

    boolean existsByAssetIdAndStatus(Long assetId, WorkOrderStatus status);
}
