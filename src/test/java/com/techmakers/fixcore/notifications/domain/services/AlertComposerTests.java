package com.techmakers.fixcore.notifications.domain.services;

import com.techmakers.fixcore.notifications.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AlertComposerTests {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 27);

    private final AlertSources sources = new AlertSources(Map.of(1L, "Compresor"),
            List.of(new PendingOrder(10L, "OT-2026-0010", 1L, 7L, false, Instant.parse("2026-10-26T08:00:00Z")),
                    new PendingOrder(11L, "OT-2026-0011", 1L, 8L, true, Instant.parse("2026-10-26T09:00:00Z"))),
            List.of(new OpenFailure(5L, 1L, true, Instant.parse("2026-10-25T08:00:00Z")),
                    new OpenFailure(6L, 1L, false, Instant.parse("2026-10-25T09:00:00Z"))),
            List.of(new UpcomingPlan(3L, "Lubricación", 1L, TODAY.minusDays(2), true),
                    new UpcomingPlan(4L, "Inspección", 1L, TODAY.plusDays(3), false)),
            List.of(new ScarcePart(9L, "Rodamiento", 0, 4, true)));

    @Test
    void shouldGiveTechniciansTheirOrdersAndCriticalFailures() {
        var alerts = AlertComposer.compose(new Recipient(7L, RecipientRole.TECHNICIAN, 1L), sources, TODAY, ZoneOffset.UTC);

        assertThat(alerts).extracting(Alert::id).containsExactly("failure-5", "wo-10");
        assertThat(alerts.getFirst().link()).isEqualTo("/assets/1");
        assertThat(alerts.get(1).params()).containsEntry("code", "OT-2026-0010").containsEntry("asset", "Compresor");
    }

    @Test
    void shouldGiveManagersPlansAndStockSortedBySeverity() {
        var alerts = AlertComposer.compose(new Recipient(2L, RecipientRole.PLANT_MANAGER, 1L), sources, TODAY, ZoneOffset.UTC);

        assertThat(alerts).extracting(Alert::kind).containsExactly(AlertKind.CRITICAL_FAILURE,
                AlertKind.MAINTENANCE_OVERDUE, AlertKind.LOW_STOCK, AlertKind.MAINTENANCE_DUE);
        assertThat(alerts.get(1).params()).containsEntry("days", 2L);
        assertThat(alerts.get(2).id()).isEqualTo("stock-9-0");
        assertThat(alerts.getFirst().link()).isEqualTo("/failures/5");
    }
}
