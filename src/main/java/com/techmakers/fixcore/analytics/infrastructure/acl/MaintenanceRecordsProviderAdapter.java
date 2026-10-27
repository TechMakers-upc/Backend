package com.techmakers.fixcore.analytics.infrastructure.acl;

import com.techmakers.fixcore.analytics.application.outboundservices.acl.MaintenanceRecordsProvider;
import com.techmakers.fixcore.analytics.domain.model.valueobjects.*;
import com.techmakers.fixcore.assetmanagement.interfaces.acl.AssetsContextFacade;
import com.techmakers.fixcore.iam.interfaces.acl.IamContextFacade;
import com.techmakers.fixcore.inventory.interfaces.acl.InventoryContextFacade;
import com.techmakers.fixcore.serviceexecution.interfaces.acl.ExecutionContextFacade;
import com.techmakers.fixcore.serviceplanning.interfaces.acl.PlanningContextFacade;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class MaintenanceRecordsProviderAdapter implements MaintenanceRecordsProvider {

    private static final String CRITICAL = "CRITICAL";

    private final AssetsContextFacade assetsContextFacade;
    private final ExecutionContextFacade executionContextFacade;
    private final PlanningContextFacade planningContextFacade;
    private final InventoryContextFacade inventoryContextFacade;
    private final IamContextFacade iamContextFacade;

    public MaintenanceRecordsProviderAdapter(AssetsContextFacade assetsContextFacade,
                                             ExecutionContextFacade executionContextFacade,
                                             PlanningContextFacade planningContextFacade,
                                             InventoryContextFacade inventoryContextFacade,
                                             IamContextFacade iamContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
        this.executionContextFacade = executionContextFacade;
        this.planningContextFacade = planningContextFacade;
        this.inventoryContextFacade = inventoryContextFacade;
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    public List<Long> allPlantIds() {
        return assetsContextFacade.fetchAllPlantIds();
    }

    @Override
    public List<MachineRecord> machinesOf(Collection<Long> plantIds) {
        return assetsContextFacade.fetchAssetsOfPlants(plantIds).stream()
                .map(asset -> new MachineRecord(asset.id(), asset.plantId(), MachineStatus.valueOf(asset.status())))
                .toList();
    }

    @Override
    public List<FailureRecord> failuresOf(Collection<Long> plantIds) {
        return executionContextFacade.fetchFailuresOfPlants(plantIds).stream()
                .map(failure -> new FailureRecord(failure.id(), failure.plantId(), failure.assetId(),
                        CRITICAL.equals(failure.priority()), failure.resolved(), failure.downtimeHours(),
                        failure.reportedAt()))
                .toList();
    }

    @Override
    public List<OrderRecord> ordersOf(Collection<Long> plantIds) {
        return executionContextFacade.fetchWorkOrdersOfPlants(plantIds).stream()
                .map(order -> new OrderRecord(order.id(), order.plantId(), order.technicianId(),
                        OrderType.valueOf(order.type()), OrderStatus.valueOf(order.status()),
                        CRITICAL.equals(order.priority()), order.dueDate(), order.createdAt(), order.completedAt(),
                        order.repairHours(), order.lastActivity()))
                .toList();
    }

    @Override
    public List<PlanRecord> plansOf(Collection<Long> plantIds) {
        return planningContextFacade.fetchPlansOfPlants(plantIds).stream()
                .map(plan -> new PlanRecord(plan.id(), plan.plantId(), "OVERDUE".equals(plan.standing())))
                .toList();
    }

    @Override
    public List<PartRecord> partsOf(Collection<Long> plantIds) {
        return inventoryContextFacade.fetchPartsOfPlants(plantIds).stream()
                .map(part -> new PartRecord(part.id(), part.plantId(), !"OK".equals(part.level())))
                .toList();
    }

    @Override
    public long techniciansOf(Collection<Long> plantIds) {
        return iamContextFacade.countTechniciansInPlants(plantIds);
    }
}
