package com.techmakers.fixcore.notifications.domain.services;

import com.techmakers.fixcore.notifications.domain.model.valueobjects.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class AlertComposer {

    private static final Comparator<Alert> BY_SEVERITY_THEN_NEWEST = Comparator
            .comparing(Alert::severity)
            .thenComparing(Alert::at, Comparator.nullsLast(Comparator.reverseOrder()));

    private AlertComposer() {
    }

    public static List<Alert> compose(Recipient recipient, AlertSources sources, LocalDate today, ZoneId zone) {
        var alerts = new ArrayList<Alert>();
        if (recipient.isTechnician()) {
            sources.pendingOrders().stream()
                    .filter(order -> recipient.userId().equals(order.technicianId()))
                    .forEach(order -> alerts.add(new Alert("wo-" + order.id(), AlertKind.WORK_ORDER_ASSIGNED,
                            order.critical() ? AlertSeverity.DOWN : AlertSeverity.INFO,
                            Map.of("code", order.code(), "asset", sources.assetName(order.assetId())),
                            "/work-orders/" + order.id(), order.createdAt())));
        }
        sources.openFailures().stream().filter(OpenFailure::critical)
                .forEach(failure -> alerts.add(new Alert("failure-" + failure.id(), AlertKind.CRITICAL_FAILURE,
                        AlertSeverity.DOWN, Map.of("asset", sources.assetName(failure.assetId())),
                        recipient.isTechnician() ? "/assets/" + failure.assetId() : "/failures/" + failure.id(),
                        failure.reportedAt())));
        if (!recipient.isTechnician()) {
            sources.upcomingPlans().forEach(plan -> alerts.add(planAlert(plan, sources, today, zone)));
            sources.scarceParts().forEach(part -> alerts.add(new Alert("stock-" + part.id() + "-" + part.stock(),
                    AlertKind.LOW_STOCK, part.out() ? AlertSeverity.DOWN : AlertSeverity.WARN,
                    Map.of("part", part.name(), "stock", part.stock(), "min", part.minStock()),
                    "/inventory/" + part.id(), null)));
        }
        alerts.sort(BY_SEVERITY_THEN_NEWEST);
        return List.copyOf(alerts);
    }

    private static Alert planAlert(UpcomingPlan plan, AlertSources sources, LocalDate today, ZoneId zone) {
        var days = Math.abs(ChronoUnit.DAYS.between(today, plan.nextDueDate()));
        return new Alert("plan-" + plan.id() + "-" + plan.nextDueDate(),
                plan.overdue() ? AlertKind.MAINTENANCE_OVERDUE : AlertKind.MAINTENANCE_DUE,
                plan.overdue() ? AlertSeverity.DOWN : AlertSeverity.WARN,
                Map.of("title", plan.title(), "asset", sources.assetName(plan.assetId()), "days", days),
                "/maintenance", plan.nextDueDate().atStartOfDay(zone).toInstant());
    }
}
