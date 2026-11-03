package com.techmakers.fixcore.inventory.application.internal.commandservices;

import com.techmakers.fixcore.inventory.application.outboundservices.acl.UserDirectory;
import com.techmakers.fixcore.inventory.application.outboundservices.acl.WorkOrderDirectory;
import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.aggregates.StockMovement;
import com.techmakers.fixcore.inventory.domain.model.commands.AdjustStockCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.ConsumeStockCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.ReceiveStockCommand;
import com.techmakers.fixcore.inventory.domain.repositories.SparePartRepository;
import com.techmakers.fixcore.inventory.domain.repositories.StockMovementRepository;
import com.techmakers.fixcore.inventory.domain.services.StockCommandService;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class StockCommandServiceImpl implements StockCommandService {

    private final SparePartRepository partRepository;
    private final StockMovementRepository movementRepository;
    private final UserDirectory userDirectory;
    private final PlantAccessGuard plantAccessGuard;
    private final WorkOrderDirectory workOrderDirectory;

    public StockCommandServiceImpl(SparePartRepository partRepository, StockMovementRepository movementRepository,
                                   UserDirectory userDirectory, PlantAccessGuard plantAccessGuard,
                                   WorkOrderDirectory workOrderDirectory) {
        this.partRepository = partRepository;
        this.movementRepository = movementRepository;
        this.userDirectory = userDirectory;
        this.plantAccessGuard = plantAccessGuard;
        this.workOrderDirectory = workOrderDirectory;
    }

    @Override
    public SparePart handle(ReceiveStockCommand command) {
        var part = findPart(command.partId());
        part.receive(command.quantity());
        var saved = partRepository.save(part);
        movementRepository.save(StockMovement.entry(saved, command.quantity(), command.reason(),
                userOf(command.performedBy()), Instant.now()));
        return saved;
    }

    @Override
    public SparePart handle(AdjustStockCommand command) {
        var part = findPart(command.partId());
        var delta = part.adjustTo(command.countedStock());
        var saved = partRepository.save(part);
        movementRepository.save(StockMovement.adjustment(saved, delta, command.reason(),
                userOf(command.performedBy()), Instant.now()));
        return saved;
    }

    @Override
    public SparePart handle(ConsumeStockCommand command) {
        var part = findPart(command.partId());
        validateWorkOrder(command, part);
        part.consume(command.quantity());
        var saved = partRepository.save(part);
        movementRepository.save(StockMovement.consumption(saved, command.quantity(), command.reason(),
                userOf(command.performedBy()), command.workOrderId(), Instant.now()));
        return saved;
    }

    private SparePart findPart(Long partId) {
        var part = partRepository.findById(partId).orElseThrow(() -> new ResourceNotFoundException("SparePart", partId));
        plantAccessGuard.ensureAccessTo(part.getPlantId());
        return part;
    }

    private void validateWorkOrder(ConsumeStockCommand command, SparePart part) {
        if (command.workOrderId() == null) {
            return;
        }

        var reference = workOrderDirectory.findById(command.workOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WorkOrder", command.workOrderId()));

        if (!reference.plantId().equals(part.getPlantId())) {
            throw new BusinessRuleViolationException(
                    "The work order belongs to another plant");
        }

        if (!reference.inProgress()) {
            throw new BusinessRuleViolationException(
                    "Only an in-progress work order can consume stock");
        }
    }

    private Long userOf(String email) {
        return userDirectory.userIdByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", email));
    }
}
