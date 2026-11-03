package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderStatus;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WorkOrder w where w.id = :id")
    Optional<WorkOrderEntity> findByIdForUpdate(@Param("id") Long id);

    List<WorkOrderEntity> findAllByOrderByDueDateAsc();

    List<WorkOrderEntity> findAllByPlantIdOrderByDueDateAsc(Long plantId);

    List<WorkOrderEntity> findAllByCodeStartingWith(String prefix);

    boolean existsByCode(String code);

    boolean existsByAssetIdAndStatusAndIdNot(Long assetId, WorkOrderStatus status, Long id);

    boolean existsByAssetIdAndStatus(Long assetId, WorkOrderStatus status);
}
