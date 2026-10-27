package com.techmakers.fixcore.serviceexecution.application.internal.eventhandlers;

import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.AssetCatalog;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.PartCatalog;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.PlanCatalog;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.UserDirectory;
import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.*;
import com.techmakers.fixcore.serviceexecution.domain.repositories.FailureRepository;
import com.techmakers.fixcore.serviceexecution.domain.repositories.WorkOrderRepository;
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
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "fixcore.seed.demo-work-orders.enabled", havingValue = "true")
public class DemoWorkOrdersSeeder {

    private record DemoLog(Instant at, String authorEmail, String note) {
    }

    private record DemoPart(String sku, int quantity, Instant at) {
    }

    private record DemoOrder(String code, String type, String plantName, String assetCode, Instant failureReportedAt,
                             String planTitle, String title, String description, String priority, String status,
                             String technicianEmail, LocalDate dueDate, Instant createdAt, String createdByEmail,
                             Instant startedAt, Instant completedAt, String solution, List<DemoLog> workLog,
                             List<DemoPart> partsUsed) {
    }

    private final WorkOrderRepository workOrderRepository;
    private final FailureRepository failureRepository;
    private final AssetCatalog assetCatalog;
    private final PlanCatalog planCatalog;
    private final PartCatalog partCatalog;
    private final UserDirectory userDirectory;
    private final JsonMapper jsonMapper;

    public DemoWorkOrdersSeeder(WorkOrderRepository workOrderRepository, FailureRepository failureRepository,
                                AssetCatalog assetCatalog, PlanCatalog planCatalog, PartCatalog partCatalog,
                                UserDirectory userDirectory, JsonMapper jsonMapper) {
        this.workOrderRepository = workOrderRepository;
        this.failureRepository = failureRepository;
        this.assetCatalog = assetCatalog;
        this.planCatalog = planCatalog;
        this.partCatalog = partCatalog;
        this.userDirectory = userDirectory;
        this.jsonMapper = jsonMapper;
    }

    @Order(4)
    @EventListener(ApplicationReadyEvent.class)
    public void seedDemoWorkOrders() throws IOException {
        try (var input = new ClassPathResource("seed/demo-work-orders.json").getInputStream()) {
            List<DemoOrder> demoOrders = jsonMapper.readValue(input, new TypeReference<>() {});
            demoOrders.stream()
                    .filter(demo -> !workOrderRepository.existsByCode(new WorkOrderCode(demo.code())))
                    .forEach(demo -> assetCatalog.assetIdByPlantNameAndCode(demo.plantName(), demo.assetCode())
                            .ifPresent(assetId -> seed(assetId, demo)));
        }
    }

    private void seed(Long assetId, DemoOrder demo) {
        var plantId = assetCatalog.plantIdOfAsset(assetId).orElseThrow();
        var failure = findFailure(assetId, demo.failureReportedAt());
        var planId = demo.planTitle() == null ? null : planCatalog.planIdByAssetAndTitle(assetId, demo.planTitle()).orElse(null);
        var details = new WorkOrderDetails(demo.title(), demo.description(), enumOf(Priority.class, demo.priority()),
                demo.dueDate());
        var workLog = demo.workLog().stream().map(log -> new WorkLogEntry(log.at(), userId(log.authorEmail()), log.note())).toList();
        var partsUsed = demo.partsUsed().stream()
                .flatMap(part -> partCatalog.partIdByPlantAndSku(plantId, part.sku())
                        .map(partId -> new PartUsage(partId, part.quantity(), part.at())).stream())
                .toList();
        var order = workOrderRepository.save(new WorkOrder(null, new WorkOrderCode(demo.code()),
                enumOf(WorkOrderType.class, demo.type()), plantId, assetId, failure.map(Failure::getId).orElse(null),
                planId, details, enumOf(WorkOrderStatus.class, demo.status()), userId(demo.technicianEmail()),
                demo.createdAt(), userId(demo.createdByEmail()), demo.startedAt(), demo.completedAt(), demo.solution(),
                workLog, partsUsed));
        failure.filter(found -> found.getWorkOrderId() == null).ifPresent(found -> failureRepository.save(linked(found, order)));
    }

    private Optional<Failure> findFailure(Long assetId, Instant reportedAt) {
        if (reportedAt == null) return Optional.empty();
        return failureRepository.findAllByAssetId(assetId).stream()
                .filter(failure -> failure.getReportedAt().equals(reportedAt))
                .findFirst();
    }

    private Failure linked(Failure failure, WorkOrder order) {
        return new Failure(failure.getId(), failure.getPlantId(), failure.getAssetId(), failure.getKind(),
                failure.getDescription(), failure.isLineStopped(), failure.getPriority(), failure.getStatus(),
                failure.getReportedBy(), failure.getReportedAt(), failure.getResolvedAt(), order.getId());
    }

    private Long userId(String email) {
        return email == null ? null : userDirectory.userIdByEmail(email).orElse(null);
    }

    private static <E extends Enum<E>> E enumOf(Class<E> type, String value) {
        return Enum.valueOf(type, value.toUpperCase().replace('-', '_'));
    }
}
