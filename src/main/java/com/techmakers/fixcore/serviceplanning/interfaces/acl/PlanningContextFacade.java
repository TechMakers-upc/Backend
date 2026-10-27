package com.techmakers.fixcore.serviceplanning.interfaces.acl;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.RegisterPlanCompletionCommand;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlanByIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByAssetIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByPlantIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanCommandService;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanQueryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class PlanningContextFacade {

    private final MaintenancePlanQueryService planQueryService;
    private final MaintenancePlanCommandService planCommandService;

    public PlanningContextFacade(MaintenancePlanQueryService planQueryService,
                                 MaintenancePlanCommandService planCommandService) {
        this.planQueryService = planQueryService;
        this.planCommandService = planCommandService;
    }

    public void registerPlanCompletion(Long planId, LocalDate completedOn) {
        planCommandService.handle(new RegisterPlanCompletionCommand(planId, completedOn));
    }

    public Optional<Long> fetchAssetIdOfPlan(Long planId) {
        return planQueryService.handle(new GetMaintenancePlanByIdQuery(planId)).map(plan -> plan.getDetails().assetId());
    }

    public Optional<Long> fetchPlanIdByAssetAndTitle(Long assetId, String title) {
        return planQueryService.handle(new GetMaintenancePlansByAssetIdQuery(assetId)).stream()
                .filter(plan -> plan.getDetails().title().equalsIgnoreCase(title.trim()))
                .map(MaintenancePlan::getId)
                .findFirst();
    }

    public List<PlanSummary> fetchPlansOfPlants(Collection<Long> plantIds) {
        var today = LocalDate.now();
        return plantIds.stream()
                .flatMap(plantId -> planQueryService.handle(new GetMaintenancePlansByPlantIdQuery(plantId)).stream())
                .map(plan -> new PlanSummary(plan.getId(), plan.getPlantId(), plan.getDetails().assetId(),
                        plan.getDetails().title(), plan.getDetails().nextDueDate(), plan.standing(today).name()))
                .toList();
    }
}
