package com.techmakers.fixcore.serviceexecution.domain.model.aggregates;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkOrder {

    private final Long id;
    private final WorkOrderCode code;
    private final WorkOrderType type;
    private final Long plantId;
    private final Long assetId;
    private final Long failureId;
    private final Long planId;
    private WorkOrderDetails details;
    private WorkOrderStatus status;
    private Long technicianId;
    private final Instant createdAt;
    private final Long createdBy;
    private Instant startedAt;
    private Instant completedAt;
    private String solution;
    private final List<WorkLogEntry> workLog;
    private final List<PartUsage> partsUsed;

    public WorkOrder(Long id, WorkOrderCode code, WorkOrderType type, Long plantId, Long assetId, Long failureId,
                     Long planId, WorkOrderDetails details, WorkOrderStatus status, Long technicianId,
                     Instant createdAt, Long createdBy, Instant startedAt, Instant completedAt, String solution,
                     List<WorkLogEntry> workLog, List<PartUsage> partsUsed) {
        if (code == null || type == null || status == null) throw new IllegalArgumentException("Code, type and status are required");
        if (plantId == null || assetId == null) throw new IllegalArgumentException("A work order needs a plant and an asset");
        if (details == null || createdAt == null) throw new IllegalArgumentException("Details and creation date are required");
        this.id = id;
        this.code = code;
        this.type = type;
        this.plantId = plantId;
        this.assetId = assetId;
        this.failureId = failureId;
        this.planId = planId;
        this.details = details;
        this.status = status;
        this.technicianId = technicianId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.solution = solution;
        this.workLog = new ArrayList<>(workLog == null ? List.of() : workLog);
        this.partsUsed = new ArrayList<>(partsUsed == null ? List.of() : partsUsed);
    }

    public static WorkOrder open(WorkOrderCode code, WorkOrderType type, Long plantId, Long assetId, Long failureId,
                                 Long planId, WorkOrderDetails details, Long technicianId, Long createdBy, Instant at) {
        return new WorkOrder(null, code, type, plantId, assetId, failureId, planId, details, WorkOrderStatus.PENDING,
                technicianId, at, createdBy, null, null, null, List.of(), List.of());
    }

    public boolean isOpen() {
        return status != WorkOrderStatus.COMPLETED;
    }

    public boolean isOverdue(LocalDate today) {
        return isOpen() && details.dueDate().isBefore(today);
    }

    public Double repairHours() {
        if (startedAt == null || completedAt == null) return null;
        return Duration.between(startedAt, completedAt).toMinutes() / 60.0;
    }

    public Long getId() { return id; }
    public WorkOrderCode getCode() { return code; }
    public WorkOrderType getType() { return type; }
    public Long getPlantId() { return plantId; }
    public Long getAssetId() { return assetId; }
    public Long getFailureId() { return failureId; }
    public Long getPlanId() { return planId; }
    public WorkOrderDetails getDetails() { return details; }
    public WorkOrderStatus getStatus() { return status; }
    public Long getTechnicianId() { return technicianId; }
    public Instant getCreatedAt() { return createdAt; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getSolution() { return solution; }
    public List<WorkLogEntry> getWorkLog() { return List.copyOf(workLog); }
    public List<PartUsage> getPartsUsed() { return List.copyOf(partsUsed); }
}
