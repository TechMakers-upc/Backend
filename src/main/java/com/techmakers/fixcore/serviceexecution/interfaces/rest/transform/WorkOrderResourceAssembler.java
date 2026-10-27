package com.techmakers.fixcore.serviceexecution.interfaces.rest.transform;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.CreateWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.UpdateWorkOrderDetailsCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderDetails;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderType;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.CreateWorkOrderResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.UpdateWorkOrderResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderResource.PartUsageResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderResource.WorkLogEntryResource;

import java.time.LocalDate;

import static com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler.toEnum;
import static com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler.toValue;

public final class WorkOrderResourceAssembler {

    private WorkOrderResourceAssembler() {
    }

    public static WorkOrderResource toResource(WorkOrder order) {
        var details = order.getDetails();
        var workLog = order.getWorkLog().stream()
                .map(entry -> new WorkLogEntryResource(entry.at(), entry.authorId(), entry.note())).toList();
        var partsUsed = order.getPartsUsed().stream()
                .map(usage -> new PartUsageResource(usage.partId(), usage.quantity(), usage.at())).toList();
        return new WorkOrderResource(order.getId(), order.getCode().value(), toValue(order.getType()),
                order.getPlantId(), order.getAssetId(), order.getFailureId(), order.getPlanId(), details.title(),
                details.description(), toValue(details.priority()), toValue(order.getStatus()), order.getTechnicianId(),
                details.dueDate(), order.getCreatedAt(), order.getCreatedBy(), order.getStartedAt(),
                order.getCompletedAt(), order.getSolution(), workLog, partsUsed, order.isOverdue(LocalDate.now()),
                order.repairHours());
    }

    public static CreateWorkOrderCommand toCreateCommand(CreateWorkOrderResource resource, String createdBy) {
        var details = new WorkOrderDetails(resource.title(), resource.description(),
                toEnum(Priority.class, resource.priority()), resource.dueDate());
        return new CreateWorkOrderCommand(toEnum(WorkOrderType.class, resource.type()), resource.assetId(), details,
                resource.technicianId(), resource.failureId(), resource.planId(), createdBy);
    }

    public static UpdateWorkOrderDetailsCommand toUpdateCommand(Long workOrderId, UpdateWorkOrderResource resource) {
        return new UpdateWorkOrderDetailsCommand(workOrderId, new WorkOrderDetails(resource.title(),
                resource.description(), toEnum(Priority.class, resource.priority()), resource.dueDate()));
    }
}
