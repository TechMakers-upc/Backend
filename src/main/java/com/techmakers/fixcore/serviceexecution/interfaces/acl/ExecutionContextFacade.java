package com.techmakers.fixcore.serviceexecution.interfaces.acl;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailuresQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetWorkOrdersQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkLogEntry;
import com.techmakers.fixcore.serviceexecution.domain.services.FailureQueryService;
import com.techmakers.fixcore.serviceexecution.domain.services.WorkOrderQueryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class ExecutionContextFacade {

    private final FailureQueryService failureQueryService;
    private final WorkOrderQueryService workOrderQueryService;

    public ExecutionContextFacade(FailureQueryService failureQueryService, WorkOrderQueryService workOrderQueryService) {
        this.failureQueryService = failureQueryService;
        this.workOrderQueryService = workOrderQueryService;
    }

    public List<FailureSummary> fetchFailuresOfPlants(Collection<Long> plantIds) {
        var now = Instant.now();
        return plantIds.stream()
                .flatMap(plantId -> failureQueryService.handle(new GetFailuresQuery(plantId, null, null)).stream())
                .map(failure -> toSummary(failure, now))
                .toList();
    }

    public List<WorkOrderSummary> fetchWorkOrdersOfPlants(Collection<Long> plantIds) {
        return plantIds.stream()
                .flatMap(plantId -> workOrderQueryService.handle(new GetWorkOrdersQuery(plantId, null, null, null)).stream())
                .map(this::toSummary)
                .toList();
    }

    private FailureSummary toSummary(Failure failure, Instant now) {
        return new FailureSummary(failure.getId(), failure.getPlantId(), failure.getAssetId(),
                failure.getPriority() == null ? null : failure.getPriority().name(), failure.isResolved(),
                failure.downtimeHours(now), failure.getReportedAt());
    }

    private WorkOrderSummary toSummary(WorkOrder order) {
        var lastActivity = Stream.concat(Stream.of(order.getStartedAt(), order.getCompletedAt()),
                        order.getWorkLog().stream().map(WorkLogEntry::at))
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(null);
        return new WorkOrderSummary(order.getId(), order.getCode().value(), order.getPlantId(), order.getAssetId(),
                order.getTechnicianId(), order.getType().name(), order.getStatus().name(),
                order.getDetails().priority().name(), order.getDetails().dueDate(), order.getCreatedAt(),
                order.getCompletedAt(), order.repairHours(), lastActivity);
    }
}
