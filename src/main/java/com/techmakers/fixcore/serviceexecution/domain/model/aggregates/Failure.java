package com.techmakers.fixcore.serviceexecution.domain.model.aggregates;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureKind;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureStatus;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;

import java.time.Duration;
import java.time.Instant;

public class Failure {

    private final Long id;
    private final Long plantId;
    private final Long assetId;
    private final FailureKind kind;
    private final String description;
    private final boolean lineStopped;
    private Priority priority;
    private FailureStatus status;
    private final Long reportedBy;
    private final Instant reportedAt;
    private Instant resolvedAt;
    private Long workOrderId;

    public Failure(Long id, Long plantId, Long assetId, FailureKind kind, String description, boolean lineStopped,
                   Priority priority, FailureStatus status, Long reportedBy, Instant reportedAt, Instant resolvedAt,
                   Long workOrderId) {
        if (plantId == null || assetId == null) throw new IllegalArgumentException("A failure needs a plant and an asset");
        if (kind == null || status == null || reportedAt == null) {
            throw new IllegalArgumentException("Failure kind, status and report date are required");
        }
        this.id = id;
        this.plantId = plantId;
        this.assetId = assetId;
        this.kind = kind;
        this.description = description == null ? "" : description.trim();
        this.lineStopped = lineStopped;
        this.priority = priority;
        this.status = status;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
        this.resolvedAt = resolvedAt;
        this.workOrderId = workOrderId;
    }

    public static Failure report(Long plantId, Long assetId, FailureKind kind, String description, boolean lineStopped,
                                 Long reportedBy, Instant at) {
        var priority = lineStopped ? Priority.CRITICAL : null;
        return new Failure(null, plantId, assetId, kind, description, lineStopped, priority, FailureStatus.OPEN,
                reportedBy, at, null, null);
    }

    public boolean isResolved() {
        return status == FailureStatus.RESOLVED;
    }

    public boolean needsWorkOrder() {
        return status == FailureStatus.OPEN && workOrderId == null;
    }

    public double downtimeHours(Instant now) {
        if (!lineStopped) return 0;
        var end = resolvedAt == null ? now : resolvedAt;
        return Duration.between(reportedAt, end).toMinutes() / 60.0;
    }

    public void prioritize(Priority priority) {
        if (priority == null) throw new IllegalArgumentException("Priority is required");
        if (isResolved()) throw new BusinessRuleViolationException("Resolved failures cannot be re-prioritized");
        this.priority = priority;
    }

    public void attachWorkOrder(Long workOrderId) {
        if (workOrderId == null) throw new IllegalArgumentException("Work order identifier is required");
        if (this.workOrderId != null) throw new BusinessRuleViolationException("This failure already has a work order");
        this.workOrderId = workOrderId;
        this.status = FailureStatus.IN_PROGRESS;
    }

    public void resolve(Instant at) {
        if (at == null) throw new IllegalArgumentException("Resolution date is required");
        if (isResolved()) throw new BusinessRuleViolationException("This failure is already resolved");
        this.status = FailureStatus.RESOLVED;
        this.resolvedAt = at;
    }

    public Long getId() { return id; }
    public Long getPlantId() { return plantId; }
    public Long getAssetId() { return assetId; }
    public FailureKind getKind() { return kind; }
    public String getDescription() { return description; }
    public boolean isLineStopped() { return lineStopped; }
    public Priority getPriority() { return priority; }
    public FailureStatus getStatus() { return status; }
    public Long getReportedBy() { return reportedBy; }
    public Instant getReportedAt() { return reportedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public Long getWorkOrderId() { return workOrderId; }
}
