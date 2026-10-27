package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderStatus;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderType;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "WorkOrder")
public class WorkOrderEntity extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkOrderType type;

    @Column(nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private Long assetId;

    private Long failureId;

    private Long planId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkOrderStatus status;

    private Long technicianId;

    @Column(nullable = false)
    private Instant openedAt;

    private Long openedBy;

    private Instant startedAt;

    private Instant completedAt;

    @Column(length = 2000)
    private String solution;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "WorkOrderLogEntry", joinColumns = @JoinColumn(name = "workOrderId"))
    @OrderColumn(name = "position")
    private List<WorkLogEntryEmbeddable> workLog = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "WorkOrderPartUsage", joinColumns = @JoinColumn(name = "workOrderId"))
    @OrderColumn(name = "position")
    private List<PartUsageEmbeddable> partsUsed = new ArrayList<>();

    protected WorkOrderEntity() {
    }

    public WorkOrderEntity(String code, WorkOrderType type, Long plantId, Long assetId, Long failureId, Long planId,
                           Instant openedAt, Long openedBy) {
        this.code = code;
        this.type = type;
        this.plantId = plantId;
        this.assetId = assetId;
        this.failureId = failureId;
        this.planId = planId;
        this.openedAt = openedAt;
        this.openedBy = openedBy;
    }

    public void setDetails(String title, String description, Priority priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public void setProgress(WorkOrderStatus status, Long technicianId, Instant startedAt, Instant completedAt,
                            String solution) {
        this.status = status;
        this.technicianId = technicianId;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.solution = solution;
    }

    public void setRecords(List<WorkLogEntryEmbeddable> workLog, List<PartUsageEmbeddable> partsUsed) {
        this.workLog = new ArrayList<>(workLog);
        this.partsUsed = new ArrayList<>(partsUsed);
    }

    public String getCode() { return code; }
    public WorkOrderType getType() { return type; }
    public Long getPlantId() { return plantId; }
    public Long getAssetId() { return assetId; }
    public Long getFailureId() { return failureId; }
    public Long getPlanId() { return planId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public WorkOrderStatus getStatus() { return status; }
    public Long getTechnicianId() { return technicianId; }
    public Instant getOpenedAt() { return openedAt; }
    public Long getOpenedBy() { return openedBy; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getSolution() { return solution; }
    public List<WorkLogEntryEmbeddable> getWorkLog() { return workLog; }
    public List<PartUsageEmbeddable> getPartsUsed() { return partsUsed; }
}
