package com.techmakers.fixcore.notifications.infrastructure.acl;

import com.techmakers.fixcore.assetmanagement.interfaces.acl.AssetSummary;
import com.techmakers.fixcore.assetmanagement.interfaces.acl.AssetsContextFacade;
import com.techmakers.fixcore.iam.interfaces.acl.IamContextFacade;
import com.techmakers.fixcore.inventory.interfaces.acl.InventoryContextFacade;
import com.techmakers.fixcore.notifications.application.outboundservices.acl.AlertSourcesProvider;
import com.techmakers.fixcore.notifications.domain.model.valueobjects.*;
import com.techmakers.fixcore.serviceexecution.interfaces.acl.ExecutionContextFacade;
import com.techmakers.fixcore.serviceplanning.interfaces.acl.PlanningContextFacade;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AlertSourcesProviderAdapter implements AlertSourcesProvider {

    private static final String CRITICAL = "CRITICAL";
    private static final Set<String> UPCOMING = Set.of("OVERDUE", "DUE_SOON");

    private final AssetsContextFacade assetsContextFacade;
    private final ExecutionContextFacade executionContextFacade;
    private final PlanningContextFacade planningContextFacade;
    private final InventoryContextFacade inventoryContextFacade;
    private final IamContextFacade iamContextFacade;

    public AlertSourcesProviderAdapter(AssetsContextFacade assetsContextFacade,
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
    public Optional<Recipient> recipientByEmail(String email) {
        return iamContextFacade.fetchAccountByEmail(email)
                .map(account -> new Recipient(account.id(), RecipientRole.valueOf(account.role()), account.plantId()));
    }

    @Override
    public List<Long> allPlantIds() {
        return assetsContextFacade.fetchAllPlantIds();
    }

    @Override
    public AlertSources sourcesOf(Collection<Long> plantIds) {
        var assetNames = assetsContextFacade.fetchAssetsOfPlants(plantIds).stream()
                .collect(Collectors.toMap(AssetSummary::id, AssetSummary::name));
        var pendingOrders = executionContextFacade.fetchWorkOrdersOfPlants(plantIds).stream()
                .filter(order -> "PENDING".equals(order.status()))
                .map(order -> new PendingOrder(order.id(), order.code(), order.assetId(), order.technicianId(),
                        CRITICAL.equals(order.priority()), order.createdAt()))
                .toList();
        var openFailures = executionContextFacade.fetchFailuresOfPlants(plantIds).stream()
                .filter(failure -> !failure.resolved())
                .map(failure -> new OpenFailure(failure.id(), failure.assetId(), CRITICAL.equals(failure.priority()),
                        failure.reportedAt()))
                .toList();
        var upcomingPlans = planningContextFacade.fetchPlansOfPlants(plantIds).stream()
                .filter(plan -> UPCOMING.contains(plan.standing()))
                .map(plan -> new UpcomingPlan(plan.id(), plan.title(), plan.assetId(), plan.nextDueDate(),
                        "OVERDUE".equals(plan.standing())))
                .toList();
        var scarceParts = inventoryContextFacade.fetchPartsOfPlants(plantIds).stream()
                .filter(part -> !"OK".equals(part.level()))
                .map(part -> new ScarcePart(part.id(), part.name(), part.stock(), part.minStock(), "OUT".equals(part.level())))
                .toList();
        return new AlertSources(assetNames, pendingOrders, openFailures, upcomingPlans, scarceParts);
    }
}
