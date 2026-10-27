package com.techmakers.fixcore.serviceplanning.domain.services;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.*;

public interface MaintenancePlanCommandService {

    MaintenancePlan handle(CreateMaintenancePlanCommand command);

    MaintenancePlan handle(UpdateMaintenancePlanCommand command);

    void handle(DeleteMaintenancePlanCommand command);

    MaintenancePlan handle(RescheduleMaintenancePlanCommand command);

    MaintenancePlan handle(SuspendMaintenancePlanCommand command);

    MaintenancePlan handle(ReactivateMaintenancePlanCommand command);

    MaintenancePlan handle(RegisterPlanCompletionCommand command);
}
