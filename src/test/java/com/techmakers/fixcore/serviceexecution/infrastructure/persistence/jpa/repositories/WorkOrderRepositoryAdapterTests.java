package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.*;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.configuration.JpaAuditingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({WorkOrderRepositoryAdapter.class, JpaAuditingConfiguration.class})
class WorkOrderRepositoryAdapterTests {

    private static final Instant OPENED = Instant.parse("2026-09-01T04:18:00Z");

    @Autowired
    private WorkOrderRepositoryAdapter workOrderRepository;

    @Test
    void shouldPersistExecutionRecordsAndFindLastCode() {
        var order = workOrderRepository.save(open(WorkOrderCode.of(1999, 7)));
        workOrderRepository.save(open(WorkOrderCode.of(1999, 12)));

        order.start(OPENED.plusSeconds(600));
        order.logWork("Se revisó el motor", 3L, OPENED.plusSeconds(1200));
        order.registerPartUsage(5L, 2, OPENED.plusSeconds(1800));
        order.registerPartUsage(6L, 1, OPENED.plusSeconds(2400));
        order.complete("Rodamiento cambiado", OPENED.plusSeconds(7200));
        workOrderRepository.save(order);

        var found = workOrderRepository.findById(order.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(WorkOrderStatus.COMPLETED);
        assertThat(found.getWorkLog()).extracting(WorkLogEntry::note).containsExactly("Se revisó el motor");
        assertThat(found.getPartsUsed()).extracting(PartUsage::partId).containsExactly(5L, 6L);
        assertThat(found.getCreatedAt()).isEqualTo(OPENED);
        assertThat(workOrderRepository.findLastCodeStartingWith("OT-1999-")).get()
                .extracting(WorkOrderCode::sequence).isEqualTo(12);
    }

    private static WorkOrder open(WorkOrderCode code) {
        return WorkOrder.open(code, WorkOrderType.CORRECTIVE, 500L, 5001L, null, null,
                new WorkOrderDetails("Reparar compresor", "", Priority.HIGH, LocalDate.of(2026, 9, 2)), 3L, 1L, OPENED);
    }
}
