package com.techmakers.fixcore.notifications.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.iam.interfaces.acl.IamContextFacade;
import com.techmakers.fixcore.notifications.application.outboundservices.acl.AlertReadStore;
import com.techmakers.fixcore.notifications.infrastructure.persistence.jpa.entities.AlertReadReceiptEntity;
import com.techmakers.fixcore.notifications.infrastructure.persistence.jpa.entities.AlertReadReceiptId;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class AlertReadStoreAdapter implements AlertReadStore {

    private final IamContextFacade iamContextFacade;
    private final AlertReadReceiptJpaRepository repository;

    public AlertReadStoreAdapter(
            IamContextFacade iamContextFacade,
            AlertReadReceiptJpaRepository repository) {
        this.iamContextFacade = iamContextFacade;
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> readIds(String email) {
        Long userId = userId(email);

        return repository.findAllById_UserId(userId).stream()
                .map(entity -> entity.getId().getAlertId())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void markRead(String email, String alertId) {
        Long userId = userId(email);
        var id = new AlertReadReceiptId(userId, alertId);

        if (!repository.existsById(id)) {
            repository.save(
                    new AlertReadReceiptEntity(id, Instant.now())
            );
        }
    }

    private Long userId(String email) {
        return iamContextFacade.fetchUserIdByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User", email)
                );
    }
}
