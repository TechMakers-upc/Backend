package com.techmakers.fixcore.analytics.application.internal.queryservices;

import com.techmakers.fixcore.analytics.application.outboundservices.acl.MaintenanceRecordsProvider;
import com.techmakers.fixcore.analytics.domain.model.queries.GetDowntimeRankingQuery;
import com.techmakers.fixcore.analytics.domain.model.queries.GetMaintenanceSummaryQuery;
import com.techmakers.fixcore.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.techmakers.fixcore.analytics.domain.model.valueobjects.*;
import com.techmakers.fixcore.analytics.domain.services.AnalyticsQueryService;
import com.techmakers.fixcore.analytics.domain.services.MaintenanceMetrics;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsQueryServiceImpl implements AnalyticsQueryService {

    private final MaintenanceRecordsProvider recordsProvider;
    private final PlantAccessGuard plantAccessGuard;

    public AnalyticsQueryServiceImpl(MaintenanceRecordsProvider recordsProvider, PlantAccessGuard plantAccessGuard) {
        this.recordsProvider = recordsProvider;
        this.plantAccessGuard = plantAccessGuard;
    }

    @Override
    public MaintenanceSummary handle(GetMaintenanceSummaryQuery query) {
        var plantIds = scope(query.plantId());
        var today = LocalDate.now();
        var machines = recordsProvider.machinesOf(plantIds);
        var failures = failuresWithin(plantIds, query.period());
        var orders = recordsProvider.ordersOf(plantIds).stream()
                .filter(order -> query.period().includes(order.createdAt())).toList();
        var open = orders.stream().filter(OrderRecord::isOpen).toList();
        var pending = failures.stream().filter(failure -> !failure.resolved()).toList();
        return new MaintenanceSummary(
                machines.size(),
                machines.stream().filter(machine -> machine.status() == MachineStatus.DOWN).count(),
                machines.stream().filter(machine -> machine.status() == MachineStatus.MAINTENANCE).count(),
                MaintenanceMetrics.availability(machines),
                MaintenanceMetrics.statusCounts(orders),
                open.size(),
                open.stream().filter(OrderRecord::critical).count(),
                open.stream().filter(order -> order.isOverdue(today)).count(),
                pending.size(),
                pending.stream().filter(FailureRecord::critical).count(),
                MaintenanceMetrics.downtimeHours(failures),
                MaintenanceMetrics.mttrHours(orders),
                MaintenanceMetrics.preventiveCompliance(orders, ZoneId.systemDefault()),
                recordsProvider.plansOf(plantIds).stream().filter(PlanRecord::overdue).count(),
                recordsProvider.partsOf(plantIds).stream().filter(PartRecord::belowMinimum).count(),
                recordsProvider.techniciansOf(plantIds));
    }

    @Override
    public List<DowntimeEntry> handle(GetDowntimeRankingQuery query) {
        return MaintenanceMetrics.downtimeRanking(failuresWithin(scope(query.plantId()), query.period()));
    }

    @Override
    public TechnicianPerformance handle(GetTechnicianPerformanceQuery query) {
        var orders = recordsProvider.ordersOf(scope(null)).stream()
                .filter(order -> query.period().includes(order.createdAt())).toList();
        return MaintenanceMetrics.technicianPerformance(query.technicianId(), orders, LocalDate.now());
    }

    private List<FailureRecord> failuresWithin(List<Long> plantIds, Period period) {
        return recordsProvider.failuresOf(plantIds).stream()
                .filter(failure -> period.includes(failure.reportedAt())).toList();
    }

    private List<Long> scope(Long plantId) {
        if (plantId != null) {
            plantAccessGuard.ensureAccessTo(plantId);
            return List.of(plantId);
        }
        return plantAccessGuard.restrictedPlantId().map(List::of).orElseGet(recordsProvider::allPlantIds);
    }
}
