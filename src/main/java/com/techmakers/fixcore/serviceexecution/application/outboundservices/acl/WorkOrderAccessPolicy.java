package com.techmakers.fixcore.serviceexecution.application.outboundservices.acl;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;

public interface WorkOrderAccessPolicy {

    boolean canRead(WorkOrder order);

    void ensureCanRead(WorkOrder order);

    void ensureCanExecute(WorkOrder order);
}
