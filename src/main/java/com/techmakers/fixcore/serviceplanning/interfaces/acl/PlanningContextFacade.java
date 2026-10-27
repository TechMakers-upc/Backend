package com.techmakers.fixcore.serviceplanning.interfaces.acl;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlanByIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByAssetIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlanningContextFacade {

    private final MaintenancePlanQueryService planQueryService;

    public PlanningContextFacade(MaintenancePlanQueryService planQueryService) {
        this.planQueryService = planQueryService;
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
}
