package com.techmakers.fixcore.serviceexecution.domain.model.aggregates;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.*;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkOrderTests {

    private static final Instant CREATED = Instant.parse("2026-09-01T04:18:00Z");

    private WorkOrder order(Long technicianId) {
        return WorkOrder.open(WorkOrderCode.of(2026, 1), WorkOrderType.CORRECTIVE, 1L, 10L, 7L, null,
                new WorkOrderDetails(" Reparar compresor ", "", Priority.CRITICAL, LocalDate.of(2026, 9, 1)),
                technicianId, 1L, CREATED);
    }

    @Test
    void shouldRequireTechnicianBeforeStarting() {
        var order = order(null);
        assertThatThrownBy(() -> order.start(CREATED)).isInstanceOf(BusinessRuleViolationException.class);

        order.assign(3L);
        order.start(CREATED.plusSeconds(1080));
        assertThat(order.getStatus()).isEqualTo(WorkOrderStatus.IN_PROGRESS);
        assertThat(order.getDetails().title()).isEqualTo("Reparar compresor");
    }

    @Test
    void shouldLogWorkPartsAndCompleteWithRepairHours() {
        var order = order(3L);
        assertThatThrownBy(() -> order.logWork("Nota", 3L, CREATED)).isInstanceOf(BusinessRuleViolationException.class);

        order.start(CREATED);
        order.logWork("Se revisó el rodamiento", 3L, CREATED.plusSeconds(600));
        order.registerPartUsage(5L, 2, CREATED.plusSeconds(1200));
        order.complete("Se cambió el rodamiento", CREATED.plusSeconds(8 * 3600));

        assertThat(order.getWorkLog()).hasSize(1);
        assertThat(order.getPartsUsed()).singleElement().extracting(PartUsage::quantity).isEqualTo(2);
        assertThat(order.repairHours()).isEqualTo(8.0);
        assertThat(order.isOpen()).isFalse();
        assertThatThrownBy(() -> order.assign(4L)).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void shouldDetectOverdueOpenOrdersAndFormatCodes() {
        var order = order(3L);

        assertThat(order.isOverdue(LocalDate.of(2026, 9, 2))).isTrue();
        assertThat(order.getCode().value()).isEqualTo("OT-2026-0001");
        assertThat(new WorkOrderCode("OT-2026-0042").sequence()).isEqualTo(42);
    }
}
