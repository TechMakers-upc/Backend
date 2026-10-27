package com.techmakers.fixcore.analytics.interfaces.rest.transform;

import com.techmakers.fixcore.analytics.domain.model.valueobjects.DowntimeEntry;
import com.techmakers.fixcore.analytics.domain.model.valueobjects.MaintenanceSummary;
import com.techmakers.fixcore.analytics.domain.model.valueobjects.TechnicianPerformance;
import com.techmakers.fixcore.analytics.interfaces.rest.resources.DowntimeEntryResource;
import com.techmakers.fixcore.analytics.interfaces.rest.resources.MaintenanceSummaryResource;
import com.techmakers.fixcore.analytics.interfaces.rest.resources.StatusCountsResource;
import com.techmakers.fixcore.analytics.interfaces.rest.resources.TechnicianPerformanceResource;

public final class AnalyticsResourceAssembler {

    private AnalyticsResourceAssembler() {
    }

    public static MaintenanceSummaryResource toResource(MaintenanceSummary summary) {
        var orders = summary.orders();
        return new MaintenanceSummaryResource(summary.assets(), summary.down(), summary.inMaintenance(),
                summary.availability(), new StatusCountsResource(orders.pending(), orders.inProgress(), orders.completed()),
                summary.openOrders(), summary.criticalOpenOrders(), summary.overdueOrders(), summary.pendingFailures(),
                summary.criticalFailures(), summary.downtimeHours(), summary.mttrHours(), summary.compliance(),
                summary.overduePlans(), summary.lowStock(), summary.technicians());
    }

    public static DowntimeEntryResource toResource(DowntimeEntry entry) {
        return new DowntimeEntryResource(entry.assetId(), entry.hours());
    }

    public static TechnicianPerformanceResource toResource(TechnicianPerformance performance) {
        return new TechnicianPerformanceResource(performance.technicianId(), performance.assigned(),
                performance.inProgress(), performance.completed(), performance.overdue(), performance.avgRepairHours(),
                performance.lastActivity());
    }
}
