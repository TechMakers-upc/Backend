package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.MovementType;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartCategory;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartUnit;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.configuration.JpaAuditingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({StockMovementRepositoryAdapter.class, SparePartRepositoryAdapter.class, JpaAuditingConfiguration.class})
class StockMovementRepositoryAdapterTests {

    @Autowired
    private StockMovementRepositoryAdapter movementRepository;

    @Autowired
    private SparePartRepositoryAdapter partRepository;

    @Test
    void shouldListMovementsOfPartNewestFirst() {
        var part = partRepository.save(new SparePart(700L, new SparePartDetails("CNT-9A", "Contactor 9A",
                PartCategory.ELECTRICAL, PartUnit.UNIT, 2, "B-02")));

        part.receive(8);
        movementRepository.save(StockMovement.entry(part, 8, "OC-001", 1L, Instant.parse("2026-08-17T10:00:00Z")));
        part.consume(1);
        movementRepository.save(StockMovement.consumption(part, 1, "OT-003", 3L, 12L, Instant.parse("2026-09-16T10:00:00Z")));

        var movements = movementRepository.findAllByPartId(part.getId());
        assertThat(movements).extracting(StockMovement::type)
                .containsExactly(MovementType.CONSUMPTION, MovementType.ENTRY);
        assertThat(movements.getFirst().stockAfter()).isEqualTo(7);
        assertThat(movements.getFirst().workOrderId()).isEqualTo(12L);
        assertThat(movementRepository.findAllByPlantId(700L)).hasSize(2);
        assertThat(movementRepository.existsByPartId(part.getId())).isTrue();
    }
}
