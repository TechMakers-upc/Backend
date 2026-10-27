package com.techmakers.fixcore.serviceexecution.infrastructure.acl;

import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.PlanCatalog;
import com.techmakers.fixcore.serviceplanning.interfaces.acl.PlanningContextFacade;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlanCatalogAdapter implements PlanCatalog {

    private final PlanningContextFacade planningContextFacade;

    public PlanCatalogAdapter(PlanningContextFacade planningContextFacade) {
        this.planningContextFacade = planningContextFacade;
    }

    @Override
    public Optional<Long> assetIdOfPlan(Long planId) {
        return planningContextFacade.fetchAssetIdOfPlan(planId);
    }

    @Override
    public Optional<Long> planIdByAssetAndTitle(Long assetId, String title) {
        return planningContextFacade.fetchPlanIdByAssetAndTitle(assetId, title);
    }
}
