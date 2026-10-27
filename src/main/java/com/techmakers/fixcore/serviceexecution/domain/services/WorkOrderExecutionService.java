package com.techmakers.fixcore.serviceexecution.domain.services;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.CompleteWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.LogWorkCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.RegisterPartUsageCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.StartWorkOrderCommand;

public interface WorkOrderExecutionService {

    WorkOrder handle(StartWorkOrderCommand command);

    WorkOrder handle(LogWorkCommand command);

    WorkOrder handle(RegisterPartUsageCommand command);

    WorkOrder handle(CompleteWorkOrderCommand command);
}
