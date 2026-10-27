package com.techmakers.fixcore.serviceexecution.domain.services;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.AssignWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.CreateWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.DeleteWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.UpdateWorkOrderDetailsCommand;

public interface WorkOrderCommandService {

    WorkOrder handle(CreateWorkOrderCommand command);

    WorkOrder handle(UpdateWorkOrderDetailsCommand command);

    WorkOrder handle(AssignWorkOrderCommand command);

    void handle(DeleteWorkOrderCommand command);
}
