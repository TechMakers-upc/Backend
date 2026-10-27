package com.techmakers.fixcore.analytics.domain.services;

import com.techmakers.fixcore.analytics.domain.model.valueobjects.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class MaintenanceMetrics {

    private MaintenanceMetrics() {
    }

    public static Integer availability(Collection<MachineRecord> machines) {
        if (machines.isEmpty()) return null;
        var running = machines.stream().filter(machine -> machine.status() == MachineStatus.OPERATIONAL).count();
        return (int) Math.round(running * 100.0 / machines.size());
    }

    public static double downtimeHours(Collection<FailureRecord> failures) {
        return failures.stream().mapToDouble(FailureRecord::downtimeHours).sum();
    }

    public static List<DowntimeEntry> downtimeRanking(Collection<FailureRecord> failures) {
        return failures.stream()
                .filter(failure -> failure.downtimeHours() > 0)
                .collect(Collectors.groupingBy(FailureRecord::assetId, Collectors.summingDouble(FailureRecord::downtimeHours)))
                .entrySet().stream()
                .map(entry -> new DowntimeEntry(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingDouble(DowntimeEntry::hours).reversed())
                .toList();
    }

    public static Double mttrHours(Collection<OrderRecord> orders) {
        var durations = orders.stream()
                .filter(order -> order.type() == OrderType.CORRECTIVE)
                .map(OrderRecord::repairHours)
                .filter(Objects::nonNull)
                .toList();
        return average(durations);
    }

    public static Integer preventiveCompliance(Collection<OrderRecord> orders, ZoneId zone) {
        var closed = orders.stream()
                .filter(order -> order.type() == OrderType.PREVENTIVE && order.completedAt() != null)
                .toList();
        if (closed.isEmpty()) return null;
        var onTime = closed.stream().filter(order -> !localDay(order.completedAt(), zone).isAfter(order.dueDate())).count();
        return (int) Math.round(onTime * 100.0 / closed.size());
    }

    public static StatusCounts statusCounts(Collection<OrderRecord> orders) {
        return new StatusCounts(count(orders, OrderStatus.PENDING), count(orders, OrderStatus.IN_PROGRESS),
                count(orders, OrderStatus.COMPLETED));
    }

    public static TechnicianPerformance technicianPerformance(Long technicianId, Collection<OrderRecord> orders,
                                                              LocalDate today) {
        var own = orders.stream().filter(order -> technicianId.equals(order.technicianId())).toList();
        var repairs = own.stream().map(OrderRecord::repairHours).filter(Objects::nonNull).toList();
        var lastActivity = own.stream().map(OrderRecord::lastActivity).filter(Objects::nonNull)
                .max(Instant::compareTo).orElse(null);
        return new TechnicianPerformance(technicianId, count(own, OrderStatus.PENDING), count(own, OrderStatus.IN_PROGRESS),
                count(own, OrderStatus.COMPLETED), own.stream().filter(order -> order.isOverdue(today)).count(),
                average(repairs), lastActivity);
    }

    private static long count(Collection<OrderRecord> orders, OrderStatus status) {
        return orders.stream().filter(order -> order.status() == status).count();
    }

    private static Double average(List<Double> values) {
        return values.isEmpty() ? null : values.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
    }

    private static LocalDate localDay(Instant at, ZoneId zone) {
        return at.atZone(zone).toLocalDate();
    }
}
