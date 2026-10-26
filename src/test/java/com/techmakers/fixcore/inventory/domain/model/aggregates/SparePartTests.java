package com.techmakers.fixcore.inventory.domain.model.aggregates;

import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartCategory;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartUnit;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.StockLevel;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SparePartTests {

    private final SparePart part = new SparePart(1L, new SparePartDetails(" rd-6205 ", "Rodamiento 6205",
            PartCategory.MECHANICAL, PartUnit.UNIT, 6, "A-01-03"));

    @Test
    void shouldReportStockLevelAgainstMinimum() {
        assertThat(part.getDetails().sku()).isEqualTo("RD-6205");
        assertThat(part.level()).isEqualTo(StockLevel.OUT);

        part.receive(6);
        assertThat(part.level()).isEqualTo(StockLevel.LOW);

        part.receive(1);
        assertThat(part.level()).isEqualTo(StockLevel.OK);
        assertThat(part.isBelowMinimum()).isFalse();
    }

    @Test
    void shouldAdjustToCountedStockAndReturnDifference() {
        part.receive(10);

        assertThat(part.adjustTo(7)).isEqualTo(-3);
        assertThat(part.getStock()).isEqualTo(7);
    }

    @Test
    void shouldNotConsumeMoreThanAvailable() {
        part.receive(2);
        part.consume(2);

        assertThat(part.getStock()).isZero();
        assertThatThrownBy(() -> part.consume(1)).isInstanceOf(BusinessRuleViolationException.class);
        assertThatThrownBy(() -> part.receive(0)).isInstanceOf(IllegalArgumentException.class);
    }
}
