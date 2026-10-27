package com.techmakers.fixcore.serviceexecution.domain.services;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetWorkOrderByIdQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetWorkOrdersQuery;

import java.util.List;
import java.util.Optional;

public interface WorkOrderQueryService {

    List<WorkOrder> handle(GetWorkOrdersQuery query);

    Optional<WorkOrder> handle(GetWorkOrderByIdQuery query);
}
