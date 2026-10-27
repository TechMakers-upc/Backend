package com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.repositories.MaintenancePlanRepository;
import com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.entities.MaintenancePlanEntity;
import com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.mappers.MaintenancePlanPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MaintenancePlanRepositoryAdapter implements MaintenancePlanRepository {

    private final MaintenancePlanJpaRepository planJpaRepository;

    public MaintenancePlanRepositoryAdapter(MaintenancePlanJpaRepository planJpaRepository) {
        this.planJpaRepository = planJpaRepository;
    }

    @Override
    public MaintenancePlan save(MaintenancePlan plan) {
        var saved = planJpaRepository.save(MaintenancePlanPersistenceMapper.toEntity(plan));
        return MaintenancePlanPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<MaintenancePlan> findById(Long id) {
        return planJpaRepository.findById(id).map(MaintenancePlanPersistenceMapper::toDomain);
    }

    @Override
    public List<MaintenancePlan> findAll() {
        return toDomain(planJpaRepository.findAllByOrderByNextDueDateAsc());
    }

    @Override
    public List<MaintenancePlan> findAllByPlantId(Long plantId) {
        return toDomain(planJpaRepository.findAllByPlantIdOrderByNextDueDateAsc(plantId));
    }

    @Override
    public List<MaintenancePlan> findAllByAssetId(Long assetId) {
        return toDomain(planJpaRepository.findAllByAssetIdOrderByNextDueDateAsc(assetId));
    }

    @Override
    public boolean existsByAssetIdAndTitle(Long assetId, String title) {
        return planJpaRepository.existsByAssetIdAndTitleIgnoreCase(assetId, title.trim());
    }

    @Override
    public boolean existsById(Long id) {
        return planJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        planJpaRepository.deleteById(id);
    }

    private List<MaintenancePlan> toDomain(List<MaintenancePlanEntity> entities) {
        return entities.stream().map(MaintenancePlanPersistenceMapper::toDomain).toList();
    }
}
