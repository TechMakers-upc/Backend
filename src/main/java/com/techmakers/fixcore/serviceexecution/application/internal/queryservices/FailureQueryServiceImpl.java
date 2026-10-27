package com.techmakers.fixcore.serviceexecution.application.internal.queryservices;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailureByIdQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailuresQuery;
import com.techmakers.fixcore.serviceexecution.domain.repositories.FailureRepository;
import com.techmakers.fixcore.serviceexecution.domain.services.FailureQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FailureQueryServiceImpl implements FailureQueryService {

    private final FailureRepository failureRepository;

    public FailureQueryServiceImpl(FailureRepository failureRepository) {
        this.failureRepository = failureRepository;
    }

    @Override
    public List<Failure> handle(GetFailuresQuery query) {
        List<Failure> failures;
        if (query.assetId() != null) failures = failureRepository.findAllByAssetId(query.assetId());
        else if (query.plantId() != null) failures = failureRepository.findAllByPlantId(query.plantId());
        else failures = failureRepository.findAll();
        return query.status() == null ? failures
                : failures.stream().filter(failure -> failure.getStatus() == query.status()).toList();
    }

    @Override
    public Optional<Failure> handle(GetFailureByIdQuery query) {
        return failureRepository.findById(query.failureId());
    }
}
