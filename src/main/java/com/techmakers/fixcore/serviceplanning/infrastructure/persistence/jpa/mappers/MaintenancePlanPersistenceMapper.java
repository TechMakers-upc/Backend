package com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.mappers;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;
import com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.entities.MaintenancePlanEntity;

public final class MaintenancePlanPersistenceMapper {

    private MaintenancePlanPersistenceMapper() {
    }

    public static MaintenancePlanEntity toEntity(MaintenancePlan plan) {
        var details = plan.getDetails();
        var entity = new MaintenancePlanEntity(plan.getPlantId(), details.assetId(), details.title(), details.tasks(),
                details.frequencyDays(), details.nextDueDate(), plan.getLastCompletedAt(), plan.isActive(),
                details.technicianId());
        entity.setId(plan.getId());
        return entity;
    }

    public static MaintenancePlan toDomain(MaintenancePlanEntity entity) {
        var details = new PlanDetails(entity.getAssetId(), entity.getTitle(), entity.getTasks(),
                entity.getFrequencyDays(), entity.getNextDueDate(), entity.getTechnicianId());
        return new MaintenancePlan(entity.getId(), entity.getPlantId(), details, entity.getLastCompletedAt(),
                entity.isActive());
    }
}
