package com.techmakers.fixcore.assetmanagement.application.internal.eventhandlers;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.*;
import com.techmakers.fixcore.assetmanagement.domain.repositories.AssetRepository;
import com.techmakers.fixcore.assetmanagement.domain.repositories.PlantRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(name = "fixcore.seed.demo-assets.enabled", havingValue = "true")
public class DemoAssetsSeeder {

    private record DemoManual(String title, String url, Instant addedAt) {
    }

    private record DemoAsset(Long plantId, String code, String name, String category, String criticality,
                             String status, Instant statusChangedAt, String location, String brand, String model,
                             String serialNumber, LocalDate installedAt, String description, List<DemoManual> manuals) {
    }

    private final AssetRepository assetRepository;
    private final PlantRepository plantRepository;
    private final JsonMapper jsonMapper;

    public DemoAssetsSeeder(AssetRepository assetRepository, PlantRepository plantRepository, JsonMapper jsonMapper) {
        this.assetRepository = assetRepository;
        this.plantRepository = plantRepository;
        this.jsonMapper = jsonMapper;
    }

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoAssets() throws IOException {
        try (var input = new ClassPathResource("seed/demo-assets.json").getInputStream()) {
            List<DemoAsset> demoAssets = jsonMapper.readValue(input, new TypeReference<>() {});
            demoAssets.stream()
                    .filter(demo -> plantRepository.existsById(demo.plantId()))
                    .filter(demo -> !assetRepository.existsByPlantIdAndCode(demo.plantId(), demo.code()))
                    .forEach(demo -> assetRepository.save(toAsset(demo)));
        }
    }

    private Asset toAsset(DemoAsset demo) {
        var details = new AssetDetails(demo.code(), demo.name(), AssetCategory.valueOf(demo.category().toUpperCase()),
                Criticality.valueOf(demo.criticality().toUpperCase()), demo.location(), demo.brand(), demo.model(),
                demo.serialNumber(), demo.installedAt(), demo.description());
        var manuals = demo.manuals().stream().map(m -> new Manual(m.title(), m.url(), m.addedAt())).toList();
        return new Asset(null, demo.plantId(), details, AssetStatus.valueOf(demo.status().toUpperCase()),
                demo.statusChangedAt(), manuals);
    }
}
