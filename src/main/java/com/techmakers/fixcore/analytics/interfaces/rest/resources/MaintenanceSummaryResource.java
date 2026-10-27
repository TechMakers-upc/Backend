package com.techmakers.fixcore.analytics.interfaces.rest.resources;

public record MaintenanceSummaryResource(
        long assets,
        long down,
        long inMaintenance,
        Integer availability,
        StatusCountsResource orders,
        long openOrders,
        long criticalOpenOrders,
        long overdueOrders,
        long pendingFailures,
        long criticalFailures,
        double downtimeHours,
        Double mttrHours,
        Integer compliance,
        long overduePlans,
        long lowStock,
        long technicians
) {
}
