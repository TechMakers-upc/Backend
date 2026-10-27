package com.techmakers.fixcore.inventory.application.internal.eventhandlers;

import com.techmakers.fixcore.inventory.application.outboundservices.acl.PlantCatalog;
import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartCategory;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartUnit;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;
import com.techmakers.fixcore.inventory.domain.repositories.SparePartRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

@Component
@ConditionalOnProperty(name = "fixcore.seed.demo-spare-parts.enabled", havingValue = "true")
public class DemoSparePartsSeeder {

    private record DemoPart(String plantName, String sku, String name, String category, String unit,
                            int stock, int minStock, String location) {
    }

    private final SparePartRepository partRepository;
    private final PlantCatalog plantCatalog;
    private final JsonMapper jsonMapper;

    public DemoSparePartsSeeder(SparePartRepository partRepository, PlantCatalog plantCatalog, JsonMapper jsonMapper) {
        this.partRepository = partRepository;
        this.plantCatalog = plantCatalog;
        this.jsonMapper = jsonMapper;
    }

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoSpareParts() throws IOException {
        try (var input = new ClassPathResource("seed/demo-spare-parts.json").getInputStream()) {
            List<DemoPart> demoParts = jsonMapper.readValue(input, new TypeReference<>() {});
            demoParts.forEach(demo -> plantCatalog.plantIdByName(demo.plantName())
                    .filter(plantId -> !partRepository.existsByPlantIdAndSku(plantId, demo.sku()))
                    .ifPresent(plantId -> partRepository.save(toPart(plantId, demo))));
        }
    }

    private SparePart toPart(Long plantId, DemoPart demo) {
        var details = new SparePartDetails(demo.sku(), demo.name(), PartCategory.valueOf(demo.category().toUpperCase()),
                PartUnit.valueOf(demo.unit().toUpperCase()), demo.minStock(), demo.location());
        return new SparePart(null, plantId, details, demo.stock());
    }
}
