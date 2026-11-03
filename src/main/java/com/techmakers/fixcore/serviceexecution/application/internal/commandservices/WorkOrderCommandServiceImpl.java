package com.techmakers.fixcore.serviceexecution.application.internal.commandservices;

import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.AssetCatalog;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.WorkOrderCodeLock;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.PlanCatalog;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.UserDirectory;
import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.*;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderCode;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderStatus;
import com.techmakers.fixcore.serviceexecution.domain.repositories.FailureRepository;
import com.techmakers.fixcore.serviceexecution.domain.repositories.WorkOrderRepository;
import com.techmakers.fixcore.serviceexecution.domain.services.WorkOrderCommandService;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Year;

@Service
@Transactional
public class WorkOrderCommandServiceImpl implements WorkOrderCommandService {

    private final WorkOrderRepository workOrderRepository;
    private final FailureRepository failureRepository;
    private final AssetCatalog assetCatalog;
    private final UserDirectory userDirectory;
    private final PlanCatalog planCatalog;
    private final PlantAccessGuard plantAccessGuard;
    private final WorkOrderCodeLock workOrderCodeLock;

    public WorkOrderCommandServiceImpl(WorkOrderRepository workOrderRepository, FailureRepository failureRepository,
                                       AssetCatalog assetCatalog, UserDirectory userDirectory, PlanCatalog planCatalog,
                                       PlantAccessGuard plantAccessGuard, WorkOrderCodeLock workOrderCodeLock) {
        this.workOrderRepository = workOrderRepository;
        this.failureRepository = failureRepository;
        this.assetCatalog = assetCatalog;
        this.userDirectory = userDirectory;
        this.planCatalog = planCatalog;
        this.plantAccessGuard = plantAccessGuard;
        this.workOrderCodeLock = workOrderCodeLock;
    }

    @Override
    public WorkOrder handle(CreateWorkOrderCommand command) {
        var plantId = assetCatalog.plantIdOfAsset(command.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", command.assetId()));
        plantAccessGuard.ensureAccessTo(plantId);
        var creatorId = userDirectory.userIdByEmail(command.createdBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.createdBy()));
        ensureTechnician(command.technicianId(), plantId);
        var failure = command.failureId() == null ? null : failureRepository.findById(command.failureId())
                .orElseThrow(() -> new ResourceNotFoundException("Failure", command.failureId()));
        if (failure != null && !failure.getAssetId().equals(command.assetId())) {
            throw new BusinessRuleViolationException("The failure belongs to another asset");
        }
        if (command.planId() != null && !planCatalog.assetIdOfPlan(command.planId())
                .orElseThrow(() -> new ResourceNotFoundException("MaintenancePlan", command.planId()))
                .equals(command.assetId())) {
            throw new BusinessRuleViolationException("The maintenance plan belongs to another asset");
        }
        var order = workOrderRepository.save(WorkOrder.open(nextCode(), command.type(), plantId, command.assetId(),
                command.failureId(), command.planId(), command.details(), command.technicianId(), creatorId, Instant.now()));
        if (failure != null) {
            failure.attachWorkOrder(order.getId());
            failureRepository.save(failure);
        }
        return order;
    }

    @Override
    public WorkOrder handle(UpdateWorkOrderDetailsCommand command) {
        var order = findOrder(command.workOrderId());
        order.updateDetails(command.details());
        return workOrderRepository.save(order);
    }

    @Override
    public WorkOrder handle(AssignWorkOrderCommand command) {
        var order = findOrder(command.workOrderId());
        ensureTechnician(command.technicianId(), order.getPlantId());
        order.assign(command.technicianId());
        return workOrderRepository.save(order);
    }

    @Override
    public void handle(DeleteWorkOrderCommand command) {
        var order = findOrder(command.workOrderId());
        if (order.getStatus() != WorkOrderStatus.PENDING || order.getFailureId() != null) {
            throw new BusinessRuleViolationException("Only pending work orders without a failure can be deleted");
        }
        workOrderRepository.deleteById(order.getId());
    }

    private WorkOrderCode nextCode() {
        workOrderCodeLock.acquire();
        var year = Year.now().getValue();
        var last = workOrderRepository.findLastCodeStartingWith(WorkOrderCode.prefixFor(year))
                .map(WorkOrderCode::sequence).orElse(0);
        return WorkOrderCode.of(year, last + 1);
    }

    private void ensureTechnician(Long technicianId, Long plantId) {
        if (technicianId == null) {
            return;
        }

        if (!userDirectory.isTechnician(technicianId)) {
            throw new BusinessRuleViolationException(
                    "User %d is not a technician".formatted(technicianId));
        }

        if (userDirectory.technicianPlantId(technicianId)
                .filter(plantId::equals)
                .isEmpty()) {
            throw new BusinessRuleViolationException(
                    "Technician %d does not belong to the work order plant"
                            .formatted(technicianId));
        }
    }

    private WorkOrder findOrder(Long workOrderId) {
        var order = workOrderRepository.findByIdForUpdate(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", workOrderId));
        plantAccessGuard.ensureAccessTo(order.getPlantId());
        return order;
    }
}
