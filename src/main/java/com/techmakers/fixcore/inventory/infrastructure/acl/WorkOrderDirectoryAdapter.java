package com.techmakers.fixcore.inventory.infrastructure.acl;

import com.techmakers.fixcore.inventory.application.outboundservices.acl.WorkOrderDirectory;
import com.techmakers.fixcore.serviceexecution.interfaces.acl.ExecutionContextFacade;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WorkOrderDirectoryAdapter implements WorkOrderDirectory {

    private final ExecutionContextFacade executionContextFacade;

    public WorkOrderDirectoryAdapter(ExecutionContextFacade executionContextFacade) {
        this.executionContextFacade = executionContextFacade;
    }

    @Override
    public Optional<Reference> findById(Long workOrderId) {
        return executionContextFacade.fetchWorkOrderById(workOrderId)
                .map(order -> new Reference(
                        order.plantId(),
                        "IN_PROGRESS".equals(order.status())));
    }
}
