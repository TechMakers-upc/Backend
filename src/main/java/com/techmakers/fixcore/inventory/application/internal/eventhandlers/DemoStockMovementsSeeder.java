package com.techmakers.fixcore.inventory.application.internal.eventhandlers;

import com.techmakers.fixcore.inventory.application.outboundservices.acl.PlantCatalog;
import com.techmakers.fixcore.inventory.application.outboundservices.acl.UserDirectory;
import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.MovementType;
import com.techmakers.fixcore.inventory.domain.repositories.SparePartRepository;
import com.techmakers.fixcore.inventory.domain.repositories.StockMovementRepository;
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
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "fixcore.seed.demo-stock-movements.enabled", havingValue = "true")
public class DemoStockMovementsSeeder {

    private record DemoMovement(String plantName, String sku, String type, int quantity, int stockAfter,
                                String reason, Instant at, String userEmail) {
    }

    private final StockMovementRepository movementRepository;
    private final SparePartRepository partRepository;
    private final PlantCatalog plantCatalog;
    private final UserDirectory userDirectory;
    private final JsonMapper jsonMapper;

    public DemoStockMovementsSeeder(StockMovementRepository movementRepository, SparePartRepository partRepository,
                                    PlantCatalog plantCatalog, UserDirectory userDirectory, JsonMapper jsonMapper) {
        this.movementRepository = movementRepository;
        this.partRepository = partRepository;
        this.plantCatalog = plantCatalog;
        this.userDirectory = userDirectory;
        this.jsonMapper = jsonMapper;
    }

    @Order(3)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoMovements() throws IOException {
        try (var input = new ClassPathResource("seed/demo-stock-movements.json").getInputStream()) {
            List<DemoMovement> demoMovements = jsonMapper.readValue(input, new TypeReference<>() {});
            var seededParts = demoMovements.stream()
                    .map(demo -> findPart(demo).map(SparePart::getId).orElse(null))
                    .filter(partId -> partId != null && movementRepository.existsByPartId(partId))
                    .distinct().toList();
            demoMovements.forEach(demo -> findPart(demo)
                    .filter(part -> !seededParts.contains(part.getId()))
                    .ifPresent(part -> movementRepository.save(toMovement(part, demo))));
        }
    }

    private Optional<SparePart> findPart(DemoMovement demo) {
        return plantCatalog.plantIdByName(demo.plantName())
                .flatMap(plantId -> partRepository.findAllByPlantId(plantId).stream()
                        .filter(part -> part.getDetails().sku().equals(demo.sku()))
                        .findFirst());
    }

    private StockMovement toMovement(SparePart part, DemoMovement demo) {
        var userId = demo.userEmail() == null ? null : userDirectory.userIdByEmail(demo.userEmail()).orElse(null);
        return new StockMovement(null, part.getPlantId(), part.getId(), MovementType.valueOf(demo.type().toUpperCase()),
                demo.quantity(), demo.stockAfter(), demo.reason(), demo.at(), userId, null);
    }
}
