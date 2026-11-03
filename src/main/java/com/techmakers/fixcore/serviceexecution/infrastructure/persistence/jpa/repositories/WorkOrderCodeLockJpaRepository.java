package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.WorkOrderCodeLockEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WorkOrderCodeLockJpaRepository
        extends JpaRepository<WorkOrderCodeLockEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select counter from WorkOrderCodeLock counter where counter.id = 1")
    Optional<WorkOrderCodeLockEntity> acquireCodeLock();
}
