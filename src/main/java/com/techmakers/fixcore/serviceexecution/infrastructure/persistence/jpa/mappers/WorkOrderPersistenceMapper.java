package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.mappers;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.PartUsage;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkLogEntry;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderCode;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderDetails;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.PartUsageEmbeddable;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.WorkLogEntryEmbeddable;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.WorkOrderEntity;

public final class WorkOrderPersistenceMapper {

    private WorkOrderPersistenceMapper() {
    }

    public static WorkOrderEntity toEntity(WorkOrder order) {
        var entity = new WorkOrderEntity(order.getCode().value(), order.getType(), order.getPlantId(), order.getAssetId(),
                order.getFailureId(), order.getPlanId(), order.getCreatedAt(), order.getCreatedBy());
        entity.setId(order.getId());
        var details = order.getDetails();
        entity.setDetails(details.title(), details.description(), details.priority(), details.dueDate());
        entity.setProgress(order.getStatus(), order.getTechnicianId(), order.getStartedAt(), order.getCompletedAt(),
                order.getSolution());
        entity.setRecords(
                order.getWorkLog().stream().map(e -> new WorkLogEntryEmbeddable(e.at(), e.authorId(), e.note())).toList(),
                order.getPartsUsed().stream().map(u -> new PartUsageEmbeddable(u.partId(), u.quantity(), u.at())).toList());
        return entity;
    }

    public static WorkOrder toDomain(WorkOrderEntity entity) {
        var details = new WorkOrderDetails(entity.getTitle(), entity.getDescription(), entity.getPriority(),
                entity.getDueDate());
        var workLog = entity.getWorkLog().stream()
                .map(e -> new WorkLogEntry(e.getOccurredAt(), e.getAuthorId(), e.getNote())).toList();
        var partsUsed = entity.getPartsUsed().stream()
                .map(u -> new PartUsage(u.getPartId(), u.getQuantity(), u.getOccurredAt())).toList();
        return new WorkOrder(entity.getId(), new WorkOrderCode(entity.getCode()), entity.getType(), entity.getPlantId(),
                entity.getAssetId(), entity.getFailureId(), entity.getPlanId(), details, entity.getStatus(),
                entity.getTechnicianId(), entity.getOpenedAt(), entity.getOpenedBy(), entity.getStartedAt(),
                entity.getCompletedAt(), entity.getSolution(), workLog, partsUsed);
    }
}
