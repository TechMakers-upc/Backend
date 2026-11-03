package com.techmakers.fixcore.serviceexecution.application.internal.queryservices;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailureByIdQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailuresQuery;
import com.techmakers.fixcore.serviceexecution.domain.repositories.FailureRepository;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.serviceexecution.domain.services.FailureQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FailureQueryServiceImpl implements FailureQueryService {

    private final FailureRepository failureRepository;
    private final PlantAccessGuard plantAccessGuard;

    public FailureQueryServiceImpl(FailureRepository failureRepository,
                                   PlantAccessGuard plantAccessGuard) {
        this.failureRepository = failureRepository;
        this.plantAccessGuard = plantAccessGuard;
    }

    @Override
    public List<Failure> handle(GetFailuresQuery query) {
        if (query.plantId() != null) {
            plantAccessGuard.ensureAccessTo(query.plantId());
        }

        List<Failure> failures;
        if (query.assetId() != null) failures = failureRepository.findAllByAssetId(query.assetId());
        else if (query.plantId() != null) failures = failureRepository.findAllByPlantId(query.plantId());
        else failures = failureRepository.findAll();

        return failures.stream()
                .filter(failure -> canRead(failure.getPlantId()))
                .filter(failure -> query.plantId() == null || query.plantId().equals(failure.getPlantId()))
                .filter(failure -> query.status() == null || failure.getStatus() == query.status())
                .toList();
    }

    @Override
    public Optional<Failure> handle(GetFailureByIdQuery query) {
        return failureRepository.findById(query.failureId())
                .map(failure -> {
                    plantAccessGuard.ensureAccessTo(failure.getPlantId());
                    return failure;
                });
    }

    private boolean canRead(Long plantId) {
        return plantAccessGuard.restrictedPlantId()
                .map(restricted -> restricted.equals(plantId))
                .orElse(true);
    }
}
