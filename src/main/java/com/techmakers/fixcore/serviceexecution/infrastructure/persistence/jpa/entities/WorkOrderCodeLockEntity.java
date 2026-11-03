package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "WorkOrderCodeLock")
public class WorkOrderCodeLockEntity {

    @Id
    @Column(nullable = false)
    private Long id;

    protected WorkOrderCodeLockEntity() {
    }

    public Long getId() {
        return id;
    }
}
