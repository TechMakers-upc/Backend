package com.techmakers.fixcore.notifications.domain.model.valueobjects;

import java.util.List;
import java.util.Map;

public record AlertSources(Map<Long, String> assetNames, List<PendingOrder> pendingOrders,
                           List<OpenFailure> openFailures, List<UpcomingPlan> upcomingPlans,
                           List<ScarcePart> scarceParts) {

    public String assetName(Long assetId) {
        return assetNames.getOrDefault(assetId, "");
    }
}
