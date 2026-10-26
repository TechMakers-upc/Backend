package com.techmakers.fixcore.serviceplanning.application.internal.eventhandlers;

import com.techmakers.fixcore.serviceplanning.application.outboundservices.acl.AssetCatalog;
import com.techmakers.fixcore.serviceplanning.application.outboundservices.acl.TechnicianDirectory;
import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;
import com.techmakers.fixcore.serviceplanning.domain.repositories.MaintenancePlanRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(name = "fixcore.seed.demo-maintenance-plans.enabled", havingValue = "true")
public class DemoMaintenancePlansSeeder {

    private record DemoPlan(String plantName, String assetCode, String technicianEmail, String title, String tasks,
                            int frequencyDays, LocalDate nextDueDate, LocalDate lastCompletedAt, boolean active) {
    }

    private final MaintenancePlanRepository planRepository;
    private final AssetCatalog assetCatalog;
    private final TechnicianDirectory technicianDirectory;
    private final JsonMapper jsonMapper;

    public DemoMaintenancePlansSeeder(MaintenancePlanRepository planRepository, AssetCatalog assetCatalog,
                                      TechnicianDirectory technicianDirectory, JsonMapper jsonMapper) {
        this.planRepository = planRepository;
        this.assetCatalog = assetCatalog;
        this.technicianDirectory = technicianDirectory;
        this.jsonMapper = jsonMapper;
    }

    @Order(3)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoPlans() throws IOException {
        try (var input = new ClassPathResource("seed/demo-maintenance-plans.json").getInputStream()) {
            List<DemoPlan> demoPlans = jsonMapper.readValue(input, new TypeReference<>() {});
            demoPlans.forEach(demo -> assetCatalog.assetIdByPlantNameAndCode(demo.plantName(), demo.assetCode())
                    .filter(assetId -> !planRepository.existsByAssetIdAndTitle(assetId, demo.title()))
                    .ifPresent(assetId -> planRepository.save(toPlan(assetId, demo))));
        }
    }

    private MaintenancePlan toPlan(Long assetId, DemoPlan demo) {
        var technicianId = demo.technicianEmail() == null ? null
                : technicianDirectory.technicianIdByEmail(demo.technicianEmail()).orElse(null);
        var details = new PlanDetails(assetId, demo.title(), demo.tasks(), demo.frequencyDays(), demo.nextDueDate(),
                technicianId);
        var plantId = assetCatalog.plantIdOfAsset(assetId).orElseThrow();
        return new MaintenancePlan(null, plantId, details, demo.lastCompletedAt(), demo.active());
    }
}
