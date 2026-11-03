package com.techmakers.fixcore.notifications.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.notifications.infrastructure.persistence.jpa.entities.AlertReadReceiptEntity;
import com.techmakers.fixcore.notifications.infrastructure.persistence.jpa.entities.AlertReadReceiptId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertReadReceiptJpaRepository
        extends JpaRepository<AlertReadReceiptEntity, AlertReadReceiptId> {

    List<AlertReadReceiptEntity> findAllById_UserId(Long userId);
}
