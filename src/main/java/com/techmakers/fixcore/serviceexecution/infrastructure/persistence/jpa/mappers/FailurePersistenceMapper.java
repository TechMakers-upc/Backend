package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.mappers;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.FailureEntity;

public final class FailurePersistenceMapper {

    private FailurePersistenceMapper() {
    }

    public static FailureEntity toEntity(Failure failure) {
        var entity = new FailureEntity(failure.getPlantId(), failure.getAssetId(), failure.getKind(),
                failure.getDescription(), failure.isLineStopped(), failure.getPriority(), failure.getStatus(),
                failure.getReportedBy(), failure.getReportedAt(), failure.getResolvedAt(), failure.getWorkOrderId());
        entity.setId(failure.getId());
        return entity;
    }

    public static Failure toDomain(FailureEntity entity) {
        return new Failure(entity.getId(), entity.getPlantId(), entity.getAssetId(), entity.getKind(),
                entity.getDescription(), entity.isLineStopped(), entity.getPriority(), entity.getStatus(),
                entity.getReportedBy(), entity.getReportedAt(), entity.getResolvedAt(), entity.getWorkOrderId());
    }
}
