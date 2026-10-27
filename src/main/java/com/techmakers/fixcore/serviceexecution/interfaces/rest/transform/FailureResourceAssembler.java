package com.techmakers.fixcore.serviceexecution.interfaces.rest.transform;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.PrioritizeFailureCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.ReportFailureCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureKind;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.FailureResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.ReportFailureResource;

import java.time.Instant;

import static com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler.toEnum;
import static com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler.toValue;

public final class FailureResourceAssembler {

    private FailureResourceAssembler() {
    }

    public static FailureResource toResource(Failure failure) {
        var priority = failure.getPriority() == null ? null : toValue(failure.getPriority());
        return new FailureResource(failure.getId(), failure.getPlantId(), failure.getAssetId(), toValue(failure.getKind()),
                failure.getDescription(), failure.isLineStopped(), priority, toValue(failure.getStatus()),
                failure.getReportedBy(), failure.getReportedAt(), failure.getResolvedAt(), failure.getWorkOrderId(),
                Math.round(failure.downtimeHours(Instant.now()) * 10) / 10.0, failure.needsWorkOrder());
    }

    public static ReportFailureCommand toReportCommand(ReportFailureResource resource, String reportedBy) {
        return new ReportFailureCommand(resource.assetId(), toEnum(FailureKind.class, resource.kind()),
                resource.description(), resource.lineStopped(), reportedBy);
    }

    public static PrioritizeFailureCommand toPrioritizeCommand(Long failureId, String priority) {
        return new PrioritizeFailureCommand(failureId, toEnum(Priority.class, priority));
    }
}
