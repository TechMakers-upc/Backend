package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SparePartRepositoryAdapter.class, JpaAuditingConfiguration.class})
class SparePartRepositoryAdapterTests {

    private static final long PLANT = 800L;

    @Autowired
    private SparePartRepositoryAdapter partRepository;

    @Test
    void shouldPersistStockChangesAndListByName() {
        var bearing = partRepository.save(new SparePart(PLANT, details("RD-6205", "Rodamiento 6205")));
        partRepository.save(new SparePart(PLANT, details("AC-15W40", "Aceite 15W-40")));

        bearing.receive(12);
        bearing.consume(4);
        partRepository.save(bearing);

        assertThat(partRepository.findById(bearing.getId())).get().extracting(SparePart::getStock).isEqualTo(8);
        assertThat(partRepository.findAllByPlantId(PLANT)).extracting(part -> part.getDetails().name())
                .containsExactly("Aceite 15W-40", "Rodamiento 6205");
    }

    @Test
    void shouldDetectDuplicateSkuWithinPlant() {
        var filter = partRepository.save(new SparePart(PLANT, details("FL-001", "Filtro de aire")));

        assertThat(partRepository.existsByPlantIdAndSku(PLANT, "FL-001")).isTrue();
        assertThat(partRepository.existsByPlantIdAndSkuAndIdNot(PLANT, "FL-001", filter.getId())).isFalse();
        assertThat(partRepository.existsByPlantIdAndSku(PLANT + 1, "FL-001")).isFalse();
    }

    private static SparePartDetails details(String sku, String name) {
        return new SparePartDetails(sku, name, PartCategory.MECHANICAL, PartUnit.UNIT, 5, "A-01");
    }
}
