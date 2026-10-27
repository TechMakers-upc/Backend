package com.techmakers.fixcore.serviceexecution.domain.repositories;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderCode;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository {

    WorkOrder save(WorkOrder workOrder);

    Optional<WorkOrder> findById(Long id);

    List<WorkOrder> findAll();

    List<WorkOrder> findAllByPlantId(Long plantId);

    Optional<WorkOrderCode> findLastCodeStartingWith(String prefix);

    boolean existsByCode(WorkOrderCode code);

    void deleteById(Long id);
}
