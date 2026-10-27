package com.techmakers.fixcore.serviceexecution.application.internal.eventhandlers;

import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.AssetCatalog;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.UserDirectory;
import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureKind;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureStatus;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;
import com.techmakers.fixcore.serviceexecution.domain.repositories.FailureRepository;
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

@Component
@ConditionalOnProperty(name = "fixcore.seed.demo-failures.enabled", havingValue = "true")
public class DemoFailuresSeeder {

    private record DemoFailure(String plantName, String assetCode, String kind, String description, boolean lineStopped,
                               String priority, String status, String reporterEmail, Instant reportedAt,
                               Instant resolvedAt) {
    }

    private final FailureRepository failureRepository;
    private final AssetCatalog assetCatalog;
    private final UserDirectory userDirectory;
    private final JsonMapper jsonMapper;

    public DemoFailuresSeeder(FailureRepository failureRepository, AssetCatalog assetCatalog,
                              UserDirectory userDirectory, JsonMapper jsonMapper) {
        this.failureRepository = failureRepository;
        this.assetCatalog = assetCatalog;
        this.userDirectory = userDirectory;
        this.jsonMapper = jsonMapper;
    }

    @Order(3)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoFailures() throws IOException {
        try (var input = new ClassPathResource("seed/demo-failures.json").getInputStream()) {
            List<DemoFailure> demoFailures = jsonMapper.readValue(input, new TypeReference<>() {});
            demoFailures.forEach(demo -> assetCatalog.assetIdByPlantNameAndCode(demo.plantName(), demo.assetCode())
                    .filter(assetId -> !failureRepository.existsByAssetIdAndReportedAt(assetId, demo.reportedAt()))
                    .ifPresent(assetId -> failureRepository.save(toFailure(assetId, demo))));
        }
    }

    private Failure toFailure(Long assetId, DemoFailure demo) {
        var plantId = assetCatalog.plantIdOfAsset(assetId).orElseThrow();
        var reporterId = demo.reporterEmail() == null ? null : userDirectory.userIdByEmail(demo.reporterEmail()).orElse(null);
        var priority = demo.priority() == null ? null : Priority.valueOf(demo.priority().toUpperCase());
        var status = FailureStatus.valueOf(demo.status().toUpperCase().replace('-', '_'));
        return new Failure(null, plantId, assetId, FailureKind.valueOf(demo.kind().toUpperCase().replace('-', '_')),
                demo.description(), demo.lineStopped(), priority, status, reporterId, demo.reportedAt(),
                demo.resolvedAt(), null);
    }
}
