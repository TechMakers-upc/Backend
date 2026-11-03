package com.techmakers.fixcore.serviceplanning.application.outboundservices.acl;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;

public interface MaintenancePlanCompletionAccessPolicy {

    void ensureCanComplete(MaintenancePlan plan);
}
