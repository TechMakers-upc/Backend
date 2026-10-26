package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetCategory;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetDetails;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetStatus;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.Criticality;
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
@Import({AssetRepositoryAdapter.class, PlantRepositoryAdapter.class, JpaAuditingConfiguration.class})
class AssetRepositoryAdapterTests {

    @Autowired
    private AssetRepositoryAdapter assetRepository;

    @Autowired
    private PlantRepositoryAdapter plantRepository;

    @Test
    void shouldPersistAssetWithManualsAndStatus() {
        var plantId = plantRepository.save(new Plant("Planta Lurín", "Lima", "")).getId();
        var asset = new Asset(plantId, details("PRS-02"), Instant.parse("2026-10-01T10:00:00Z"));
        asset.addManual("Manual de la prensa", "https://fixcore.demo/prensa.pdf", Instant.parse("2026-10-02T10:00:00Z"));
        asset.addManual("Plano eléctrico", "https://fixcore.demo/plano.pdf", Instant.parse("2026-10-03T10:00:00Z"));

        var saved = assetRepository.save(asset);
        saved.markInMaintenance(Instant.parse("2026-10-04T08:00:00Z"));
        saved.removeManual(0);
        assetRepository.save(saved);

        var found = assetRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(AssetStatus.MAINTENANCE);
        assertThat(found.getManuals()).singleElement().extracting("title").isEqualTo("Plano eléctrico");
        assertThat(found.getDetails().installedAt()).isEqualTo(LocalDate.of(2020, 5, 12));
        assertThat(assetRepository.findAllByPlantId(plantId)).hasSize(1);
        assertThat(assetRepository.existsByPlantId(plantId)).isTrue();
    }

    @Test
    void shouldDetectDuplicateCodeWithinPlant() {
        var plantId = plantRepository.save(new Plant("Planta Chilca", "Lima", "")).getId();
        var saved = assetRepository.save(new Asset(plantId, details("BMB-01"), Instant.now()));

        assertThat(assetRepository.existsByPlantIdAndCode(plantId, "BMB-01")).isTrue();
        assertThat(assetRepository.existsByPlantIdAndCodeAndIdNot(plantId, "BMB-01", saved.getId())).isFalse();
    }

    private static AssetDetails details(String code) {
        return new AssetDetails(code, "Prensa hidráulica", AssetCategory.PRESS, Criticality.HIGH, "Línea 2",
                "Schuler", "HP-200", "SC-2020-118", LocalDate.of(2020, 5, 12), "Prensa de estampado");
    }
}
