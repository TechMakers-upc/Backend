package com.techmakers.fixcore.inventory.domain.model.aggregates;

import com.techmakers.fixcore.inventory.domain.model.valueobjects.MovementType;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartCategory;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartUnit;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class StockMovementTests {

    private static final Instant NOW = Instant.parse("2026-10-26T15:00:00Z");

    private final SparePart part = new SparePart(5L, 1L, new SparePartDetails("RD-6205", "Rodamiento",
            PartCategory.MECHANICAL, PartUnit.UNIT, 6, "A-01"), 0);

    @Test
    void shouldRecordEntryWithResultingStock() {
        part.receive(16);
        var movement = StockMovement.entry(part, 16, " OC-2026-118 ", 1L, NOW);

        assertThat(movement.type()).isEqualTo(MovementType.ENTRY);
        assertThat(movement.quantity()).isEqualTo(16);
        assertThat(movement.stockAfter()).isEqualTo(16);
        assertThat(movement.reason()).isEqualTo("OC-2026-118");
        assertThat(movement.partId()).isEqualTo(5L);
    }

    @Test
    void shouldRecordConsumptionAsNegativeQuantity() {
        part.receive(16);
        part.consume(2);
        var movement = StockMovement.consumption(part, 2, "OT-0001", 3L, 41L, NOW);

        assertThat(movement.quantity()).isEqualTo(-2);
        assertThat(movement.stockAfter()).isEqualTo(14);
        assertThat(movement.workOrderId()).isEqualTo(41L);
    }

    @Test
    void shouldRecordAdjustmentDelta() {
        part.receive(4);
        var delta = part.adjustTo(2);
        var movement = StockMovement.adjustment(part, delta, "Conteo físico", 1L, NOW);

        assertThat(movement.quantity()).isEqualTo(-2);
        assertThat(movement.stockAfter()).isEqualTo(2);
    }
}
