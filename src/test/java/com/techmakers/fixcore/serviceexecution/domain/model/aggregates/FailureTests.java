package com.techmakers.fixcore.serviceexecution.domain.model.aggregates;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureKind;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureStatus;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FailureTests {

    private static final Instant REPORTED = Instant.parse("2026-09-01T04:00:00Z");

    @Test
    void shouldMakeStoppedLinesCritical() {
        var stopped = Failure.report(1L, 10L, FailureKind.NOISE, " Golpeteo ", true, 3L, REPORTED);
        var running = Failure.report(1L, 10L, FailureKind.LEAK, "Goteo", false, 3L, REPORTED);

        assertThat(stopped.getPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(stopped.getDescription()).isEqualTo("Golpeteo");
        assertThat(running.getPriority()).isNull();
        assertThat(running.needsWorkOrder()).isTrue();
    }

    @Test
    void shouldCountDowntimeOnlyWhileLineIsStopped() {
        var stopped = Failure.report(1L, 10L, FailureKind.JAM, "Atasco", true, 3L, REPORTED);
        stopped.resolve(REPORTED.plusSeconds(8 * 3600));
        var running = Failure.report(1L, 10L, FailureKind.LEAK, "Goteo", false, 3L, REPORTED);

        assertThat(stopped.downtimeHours(Instant.now())).isEqualTo(8.0);
        assertThat(running.downtimeHours(REPORTED.plusSeconds(3600))).isZero();
    }

    @Test
    void shouldMoveThroughWorkOrderAndResolution() {
        var failure = Failure.report(1L, 10L, FailureKind.ELECTRICAL, "Sin energía", false, 3L, REPORTED);
        failure.prioritize(Priority.HIGH);
        failure.attachWorkOrder(55L);

        assertThat(failure.getStatus()).isEqualTo(FailureStatus.IN_PROGRESS);
        assertThatThrownBy(() -> failure.attachWorkOrder(56L)).isInstanceOf(BusinessRuleViolationException.class);

        failure.resolve(REPORTED.plusSeconds(3600));
        assertThat(failure.isResolved()).isTrue();
        assertThatThrownBy(() -> failure.prioritize(Priority.LOW)).isInstanceOf(BusinessRuleViolationException.class);
    }
}
