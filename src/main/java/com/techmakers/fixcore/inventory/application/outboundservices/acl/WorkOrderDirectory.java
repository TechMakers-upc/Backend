package com.techmakers.fixcore.inventory.application.outboundservices.acl;

import java.util.Optional;

public interface WorkOrderDirectory {

    Optional<Reference> findById(Long workOrderId);

    record Reference(Long plantId, boolean inProgress) {
    }
}
