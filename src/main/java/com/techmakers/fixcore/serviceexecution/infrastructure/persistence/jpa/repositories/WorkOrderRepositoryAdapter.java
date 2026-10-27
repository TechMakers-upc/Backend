package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderCode;
import com.techmakers.fixcore.serviceexecution.domain.repositories.WorkOrderRepository;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.WorkOrderEntity;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.mappers.WorkOrderPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WorkOrderRepositoryAdapter implements WorkOrderRepository {

    private final WorkOrderJpaRepository workOrderJpaRepository;

    public WorkOrderRepositoryAdapter(WorkOrderJpaRepository workOrderJpaRepository) {
        this.workOrderJpaRepository = workOrderJpaRepository;
    }

    @Override
    public WorkOrder save(WorkOrder workOrder) {
        var saved = workOrderJpaRepository.save(WorkOrderPersistenceMapper.toEntity(workOrder));
        return WorkOrderPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<WorkOrder> findById(Long id) {
        return workOrderJpaRepository.findById(id).map(WorkOrderPersistenceMapper::toDomain);
    }

    @Override
    public List<WorkOrder> findAll() {
        return toDomain(workOrderJpaRepository.findAllByOrderByDueDateAsc());
    }

    @Override
    public List<WorkOrder> findAllByPlantId(Long plantId) {
        return toDomain(workOrderJpaRepository.findAllByPlantIdOrderByDueDateAsc(plantId));
    }

    @Override
    public Optional<WorkOrderCode> findLastCodeStartingWith(String prefix) {
        return workOrderJpaRepository.findTopByCodeStartingWithOrderByCodeDesc(prefix)
                .map(entity -> new WorkOrderCode(entity.getCode()));
    }

    @Override
    public boolean existsByCode(WorkOrderCode code) {
        return workOrderJpaRepository.existsByCode(code.value());
    }

    @Override
    public void deleteById(Long id) {
        workOrderJpaRepository.deleteById(id);
    }

    private List<WorkOrder> toDomain(List<WorkOrderEntity> entities) {
        return entities.stream().map(WorkOrderPersistenceMapper::toDomain).toList();
    }
}
