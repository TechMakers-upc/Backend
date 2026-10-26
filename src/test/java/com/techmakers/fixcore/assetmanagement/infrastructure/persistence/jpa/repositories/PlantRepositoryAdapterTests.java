package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
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
@Import({PlantRepositoryAdapter.class, JpaAuditingConfiguration.class})
class PlantRepositoryAdapterTests {

    @Autowired
    private PlantRepositoryAdapter plantRepository;

    @Test
    void shouldSaveUpdateAndListPlantsByName() {
        var callao = plantRepository.save(new Plant("Planta Callao", "Callao", "Av. Argentina 3250"));
        plantRepository.save(new Plant("Planta Ate", "Lima", "Av. Nicolás Ayllón 2480"));

        callao.updateDetails("Planta Callao Norte", "Callao", "Av. Argentina 3300");
        plantRepository.save(callao);

        assertThat(plantRepository.findAll()).extracting(Plant::getName)
                .containsExactly("Planta Ate", "Planta Callao Norte");
        assertThat(plantRepository.findById(callao.getId())).get()
                .extracting(Plant::getAddress).isEqualTo("Av. Argentina 3300");
    }

    @Test
    void shouldDetectDuplicateNamesIgnoringCase() {
        var arequipa = plantRepository.save(new Plant("Planta Arequipa", "Arequipa", ""));

        assertThat(plantRepository.existsByName("planta arequipa")).isTrue();
        assertThat(plantRepository.existsByNameAndIdNot("Planta Arequipa", arequipa.getId())).isFalse();
        assertThat(plantRepository.existsByNameAndIdNot("Planta Arequipa", arequipa.getId() + 1)).isTrue();
    }
}
