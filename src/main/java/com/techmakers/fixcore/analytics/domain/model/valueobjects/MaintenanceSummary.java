package com.techmakers.fixcore.analytics.domain.model.valueobjects;

public record MaintenanceSummary(
        long assets,
        long down,
        long inMaintenance,
        Integer availability,
        StatusCounts orders,
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
