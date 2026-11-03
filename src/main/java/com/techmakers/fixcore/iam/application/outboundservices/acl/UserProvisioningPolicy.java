package com.techmakers.fixcore.iam.application.outboundservices.acl;

import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;

public interface UserProvisioningPolicy {

    void ensureCanCreate(Roles requestedRole, Long requestedPlantId);
}
