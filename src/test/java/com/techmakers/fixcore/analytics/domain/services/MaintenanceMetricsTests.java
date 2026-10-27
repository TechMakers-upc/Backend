package com.techmakers.fixcore.analytics.domain.services;

import com.techmakers.fixcore.analytics.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class MaintenanceMetricsTests {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 27);

    @Test
    void shouldComputeAvailabilityAndDowntime() {
        var machines = List.of(new MachineRecord(1L, 1L, MachineStatus.OPERATIONAL),
                new MachineRecord(2L, 1L, MachineStatus.DOWN), new MachineRecord(3L, 1L, MachineStatus.OPERATIONAL));
        var failures = List.of(failure(1L, 2.0), failure(2L, 5.0), failure(1L, 1.5), failure(3L, 0));

        assertThat(MaintenanceMetrics.availability(machines)).isEqualTo(67);
        assertThat(MaintenanceMetrics.availability(List.of())).isNull();
        assertThat(MaintenanceMetrics.downtimeHours(failures)).isEqualTo(8.5);
        assertThat(MaintenanceMetrics.downtimeRanking(failures))
                .containsExactly(new DowntimeEntry(2L, 5.0), new DowntimeEntry(1L, 3.5));
    }

    @Test
    void shouldComputeRepairTimeComplianceAndCounts() {
        var orders = List.of(
                order(OrderType.CORRECTIVE, OrderStatus.COMPLETED, 7L, TODAY, Instant.parse("2026-10-20T10:00:00Z"), 2.0),
                order(OrderType.CORRECTIVE, OrderStatus.COMPLETED, 7L, TODAY, Instant.parse("2026-10-21T10:00:00Z"), 4.0),
                order(OrderType.PREVENTIVE, OrderStatus.COMPLETED, 7L, TODAY.minusDays(10), Instant.parse("2026-10-20T10:00:00Z"), 1.0),
                order(OrderType.PREVENTIVE, OrderStatus.COMPLETED, 8L, TODAY, Instant.parse("2026-10-20T10:00:00Z"), null),
                order(OrderType.CORRECTIVE, OrderStatus.PENDING, 7L, TODAY.minusDays(1), null, null));

        assertThat(MaintenanceMetrics.mttrHours(orders)).isEqualTo(3.0);
        assertThat(MaintenanceMetrics.preventiveCompliance(orders, ZoneOffset.UTC)).isEqualTo(50);
        assertThat(MaintenanceMetrics.statusCounts(orders)).isEqualTo(new StatusCounts(1, 0, 4));

        var performance = MaintenanceMetrics.technicianPerformance(7L, orders, TODAY);
        assertThat(performance.assigned()).isEqualTo(1);
        assertThat(performance.completed()).isEqualTo(3);
        assertThat(performance.overdue()).isEqualTo(1);
        assertThat(performance.avgRepairHours()).isCloseTo(2.33, offset(0.01));
        assertThat(performance.lastActivity()).isEqualTo(Instant.parse("2026-10-21T10:00:00Z"));
    }

    private FailureRecord failure(Long assetId, double hours) {
        return new FailureRecord(null, 1L, assetId, false, true, hours, Instant.parse("2026-10-20T08:00:00Z"));
    }

    private OrderRecord order(OrderType type, OrderStatus status, Long technicianId, LocalDate dueDate,
                              Instant completedAt, Double repairHours) {
        return new OrderRecord(null, 1L, technicianId, type, status, false, dueDate, Instant.parse("2026-10-01T08:00:00Z"),
                completedAt, repairHours, completedAt);
    }
}
