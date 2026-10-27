package com.techmakers.fixcore.serviceexecution.application.internal.commandservices;

import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.PartCatalog;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.UserDirectory;
import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.CompleteWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.LogWorkCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.RegisterPartUsageCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.StartWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.repositories.FailureRepository;
import com.techmakers.fixcore.serviceexecution.domain.repositories.WorkOrderRepository;
import com.techmakers.fixcore.serviceexecution.domain.services.WorkOrderExecutionService;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class WorkOrderExecutionServiceImpl implements WorkOrderExecutionService {

    private final WorkOrderRepository workOrderRepository;
    private final FailureRepository failureRepository;
    private final UserDirectory userDirectory;
    private final PartCatalog partCatalog;

    public WorkOrderExecutionServiceImpl(WorkOrderRepository workOrderRepository, FailureRepository failureRepository,
                                         UserDirectory userDirectory, PartCatalog partCatalog) {
        this.workOrderRepository = workOrderRepository;
        this.failureRepository = failureRepository;
        this.userDirectory = userDirectory;
        this.partCatalog = partCatalog;
    }

    @Override
    public WorkOrder handle(StartWorkOrderCommand command) {
        var order = findOrder(command.workOrderId());
        order.start(Instant.now());
        return workOrderRepository.save(order);
    }

    @Override
    public WorkOrder handle(LogWorkCommand command) {
        var order = findOrder(command.workOrderId());
        var authorId = userDirectory.userIdByEmail(command.author())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.author()));
        order.logWork(command.note(), authorId, Instant.now());
        return workOrderRepository.save(order);
    }

    @Override
    public WorkOrder handle(RegisterPartUsageCommand command) {
        var order = findOrder(command.workOrderId());
        var partPlant = partCatalog.plantIdOfPart(command.partId())
                .orElseThrow(() -> new ResourceNotFoundException("SparePart", command.partId()));
        if (!partPlant.equals(order.getPlantId())) {
            throw new BusinessRuleViolationException("The spare part belongs to another plant");
        }
        order.registerPartUsage(command.partId(), command.quantity(), Instant.now());
        return workOrderRepository.save(order);
    }

    @Override
    public WorkOrder handle(CompleteWorkOrderCommand command) {
        var order = findOrder(command.workOrderId());
        var at = Instant.now();
        order.complete(command.solution(), at);
        var saved = workOrderRepository.save(order);
        if (saved.getFailureId() != null) {
            failureRepository.findById(saved.getFailureId())
                    .filter(failure -> !failure.isResolved())
                    .ifPresent(failure -> {
                        failure.resolve(at);
                        failureRepository.save(failure);
                    });
        }
        return saved;
    }

    private WorkOrder findOrder(Long workOrderId) {
        return workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", workOrderId));
    }
}
