package com.techmakers.fixcore.serviceexecution.infrastructure.acl;

import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.PlanCompletionRecorder;
import com.techmakers.fixcore.serviceplanning.interfaces.acl.PlanningContextFacade;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PlanCompletionRecorderAdapter implements PlanCompletionRecorder {

    private final PlanningContextFacade planningContextFacade;

    public PlanCompletionRecorderAdapter(PlanningContextFacade planningContextFacade) {
        this.planningContextFacade = planningContextFacade;
    }

    @Override
    public void registerCompletion(Long planId, LocalDate completedOn) {
        planningContextFacade.registerPlanCompletion(planId, completedOn);
    }
}
