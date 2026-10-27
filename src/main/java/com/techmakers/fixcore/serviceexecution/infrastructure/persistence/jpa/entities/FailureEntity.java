package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureKind;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureStatus;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;

@Entity(name = "Failure")
public class FailureEntity extends AuditableEntity {

    @Column(nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private Long assetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FailureKind kind;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean lineStopped;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FailureStatus status;

    private Long reportedBy;

    @Column(nullable = false)
    private Instant reportedAt;

    private Instant resolvedAt;

    private Long workOrderId;

    protected FailureEntity() {
    }

    public FailureEntity(Long plantId, Long assetId, FailureKind kind, String description, boolean lineStopped,
                         Priority priority, FailureStatus status, Long reportedBy, Instant reportedAt,
                         Instant resolvedAt, Long workOrderId) {
        this.plantId = plantId;
        this.assetId = assetId;
        this.kind = kind;
        this.description = description;
        this.lineStopped = lineStopped;
        this.priority = priority;
        this.status = status;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
        this.resolvedAt = resolvedAt;
        this.workOrderId = workOrderId;
    }

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
