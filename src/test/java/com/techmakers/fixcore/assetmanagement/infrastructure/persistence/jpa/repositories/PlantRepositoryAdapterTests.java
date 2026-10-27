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
        var callao = plantRepository.save(new Plant("Repo Planta Sur", "Callao", "Av. Argentina 3250"));
        plantRepository.save(new Plant("Repo Planta Este", "Lima", "Av. Nicolás Ayllón 2480"));

        callao.updateDetails("Repo Planta Sur Norte", "Callao", "Av. Argentina 3300");
        plantRepository.save(callao);

        assertThat(plantRepository.findAll()).extracting(Plant::getName)
                .filteredOn(name -> name.startsWith("Repo"))
                .containsExactly("Repo Planta Este", "Repo Planta Sur Norte");
        assertThat(plantRepository.findById(callao.getId())).get()
                .extracting(Plant::getAddress).isEqualTo("Av. Argentina 3300");
    }

    @Test
    void shouldDetectDuplicateNamesIgnoringCase() {
        var arequipa = plantRepository.save(new Plant("Repo Planta Sierra", "Arequipa", ""));

        assertThat(plantRepository.existsByName("repo planta sierra")).isTrue();
        assertThat(plantRepository.existsByNameAndIdNot("Repo Planta Sierra", arequipa.getId())).isFalse();
        assertThat(plantRepository.existsByNameAndIdNot("Repo Planta Sierra", arequipa.getId() + 1)).isTrue();
    }
}
