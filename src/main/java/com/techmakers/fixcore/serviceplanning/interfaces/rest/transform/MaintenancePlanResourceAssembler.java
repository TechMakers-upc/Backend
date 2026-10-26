package com.techmakers.fixcore.serviceplanning.interfaces.rest.transform;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.CreateMaintenancePlanCommand;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.UpdateMaintenancePlanCommand;
import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.resources.MaintenancePlanResource;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.resources.SaveMaintenancePlanResource;
import com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler;

import java.time.LocalDate;

public final class MaintenancePlanResourceAssembler {

    private MaintenancePlanResourceAssembler() {
    }

    public static MaintenancePlanResource toResource(MaintenancePlan plan) {
        var details = plan.getDetails();
        return new MaintenancePlanResource(plan.getId(), plan.getPlantId(), details.assetId(), details.title(),
                details.tasks(), details.frequencyDays(), details.nextDueDate(), plan.getLastCompletedAt(),
                plan.isActive(), details.technicianId(),
                EnumValueAssembler.toValue(plan.standing(LocalDate.now())));
    }

    public static CreateMaintenancePlanCommand toCreateCommand(SaveMaintenancePlanResource resource) {
        return new CreateMaintenancePlanCommand(toDetails(resource));
    }

    public static UpdateMaintenancePlanCommand toUpdateCommand(Long planId, SaveMaintenancePlanResource resource) {
        return new UpdateMaintenancePlanCommand(planId, toDetails(resource));
    }

    private static PlanDetails toDetails(SaveMaintenancePlanResource resource) {
        return new PlanDetails(resource.assetId(), resource.title(), resource.tasks(), resource.frequencyDays(),
                resource.nextDueDate(), resource.technicianId());
    }
}
