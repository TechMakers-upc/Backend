package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.WorkOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderEntity, Long> {

    List<WorkOrderEntity> findAllByOrderByDueDateAsc();

    List<WorkOrderEntity> findAllByPlantIdOrderByDueDateAsc(Long plantId);

    Optional<WorkOrderEntity> findTopByCodeStartingWithOrderByCodeDesc(String prefix);

    boolean existsByCode(String code);
}
