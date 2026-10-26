package com.techmakers.fixcore.serviceplanning.domain.model.aggregates;

import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;
import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanStanding;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MaintenancePlan {

    public static final int DUE_SOON_DAYS = 7;

    private final Long id;
    private final Long plantId;
    private PlanDetails details;
    private LocalDate lastCompletedAt;
    private boolean active;

    public MaintenancePlan(Long plantId, PlanDetails details) {
        this(null, plantId, details, null, true);
    }

    public MaintenancePlan(Long id, Long plantId, PlanDetails details, LocalDate lastCompletedAt, boolean active) {
        if (plantId == null) throw new IllegalArgumentException("A plan must belong to a plant");
        if (details == null) throw new IllegalArgumentException("Plan details are required");
        this.id = id;
        this.plantId = plantId;
        this.details = details;
        this.lastCompletedAt = lastCompletedAt;
        this.active = active;
    }

    public PlanStanding standing(LocalDate today) {
        if (!active) return PlanStanding.SUSPENDED;
        var days = ChronoUnit.DAYS.between(today, details.nextDueDate());
        if (days < 0) return PlanStanding.OVERDUE;
        if (days <= DUE_SOON_DAYS) return PlanStanding.DUE_SOON;
        return PlanStanding.SCHEDULED;
    }

    public boolean isOverdue(LocalDate today) {
        return standing(today) == PlanStanding.OVERDUE;
    }

    public void updateDetails(PlanDetails details) {
        if (details == null) throw new IllegalArgumentException("Plan details are required");
        this.details = details;
    }

    public Long getId() { return id; }

    public Long getPlantId() { return plantId; }

    public PlanDetails getDetails() { return details; }

    public LocalDate getLastCompletedAt() { return lastCompletedAt; }

    public boolean isActive() { return active; }
}
