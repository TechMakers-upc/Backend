package com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.entities;

import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.entities.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity(name = "MaintenancePlan")
public class MaintenancePlanEntity extends AuditableEntity {

    @Column(nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private Long assetId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String tasks;

    @Column(nullable = false)
    private int frequencyDays;

    @Column(nullable = false)
    private LocalDate nextDueDate;

    private LocalDate lastCompletedAt;

    @Column(nullable = false)
    private boolean active;

    private Long technicianId;

    protected MaintenancePlanEntity() {
    }

    public MaintenancePlanEntity(Long plantId, Long assetId, String title, String tasks, int frequencyDays,
                                 LocalDate nextDueDate, LocalDate lastCompletedAt, boolean active, Long technicianId) {
        this.plantId = plantId;
        this.assetId = assetId;
        this.title = title;
        this.tasks = tasks;
        this.frequencyDays = frequencyDays;
        this.nextDueDate = nextDueDate;
        this.lastCompletedAt = lastCompletedAt;
        this.active = active;
        this.technicianId = technicianId;
    }

    public Long getPlantId() { return plantId; }
    public Long getAssetId() { return assetId; }
    public String getTitle() { return title; }
    public String getTasks() { return tasks; }
    public int getFrequencyDays() { return frequencyDays; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public LocalDate getLastCompletedAt() { return lastCompletedAt; }
    public boolean isActive() { return active; }
    public Long getTechnicianId() { return technicianId; }
}
