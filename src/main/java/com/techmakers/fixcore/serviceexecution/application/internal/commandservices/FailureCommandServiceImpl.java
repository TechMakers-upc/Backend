package com.techmakers.fixcore.serviceexecution.application.internal.commandservices;

import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.AssetCatalog;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.AssetStatusUpdater;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.UserDirectory;
import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.PrioritizeFailureCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.ReportFailureCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.ResolveFailureCommand;
import com.techmakers.fixcore.serviceexecution.domain.repositories.FailureRepository;
import com.techmakers.fixcore.serviceexecution.domain.services.FailureCommandService;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class FailureCommandServiceImpl implements FailureCommandService {

    private final FailureRepository failureRepository;
    private final AssetCatalog assetCatalog;
    private final UserDirectory userDirectory;
    private final AssetStatusUpdater assetStatusUpdater;
    private final PlantAccessGuard plantAccessGuard;

    public FailureCommandServiceImpl(FailureRepository failureRepository, AssetCatalog assetCatalog,
                                     UserDirectory userDirectory, AssetStatusUpdater assetStatusUpdater,
                                     PlantAccessGuard plantAccessGuard) {
        this.failureRepository = failureRepository;
        this.assetCatalog = assetCatalog;
        this.userDirectory = userDirectory;
        this.assetStatusUpdater = assetStatusUpdater;
        this.plantAccessGuard = plantAccessGuard;
    }

    @Override
    public Failure handle(ReportFailureCommand command) {
        var plantId = assetCatalog.plantIdOfAsset(command.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", command.assetId()));
        plantAccessGuard.ensureAccessTo(plantId);
        var reporterId = userDirectory.userIdByEmail(command.reportedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.reportedBy()));
        var failure = failureRepository.save(Failure.report(plantId, command.assetId(), command.kind(),
                command.description(), command.lineStopped(), reporterId, Instant.now()));
        if (failure.isLineStopped()) {
            assetStatusUpdater.markDown(failure.getAssetId());
        }
        return failure;
    }

    @Override
    public Failure handle(PrioritizeFailureCommand command) {
        var failure = findFailure(command.failureId());
        failure.prioritize(command.priority());
        return failureRepository.save(failure);
    }

    @Override
    public Failure handle(ResolveFailureCommand command) {
        var failure = findFailure(command.failureId());
        failure.resolve(Instant.now());
        return failureRepository.save(failure);
    }

    private Failure findFailure(Long failureId) {
        var failure = failureRepository.findById(failureId).orElseThrow(() -> new ResourceNotFoundException("Failure", failureId));
        plantAccessGuard.ensureAccessTo(failure.getPlantId());
        return failure;
    }
}
