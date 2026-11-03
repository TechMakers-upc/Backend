package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.WorkOrderCodeLock;
import org.springframework.stereotype.Repository;

@Repository
public class WorkOrderCodeLockAdapter implements WorkOrderCodeLock {

    private final WorkOrderCodeLockJpaRepository repository;

    public WorkOrderCodeLockAdapter(WorkOrderCodeLockJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void acquire() {
        repository.acquireCodeLock()
                .orElseThrow(() -> new IllegalStateException(
                        "Work order code lock row is missing"));
    }
}
