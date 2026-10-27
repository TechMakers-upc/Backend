package com.techmakers.fixcore.serviceexecution.domain.repositories;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FailureRepository {

    Failure save(Failure failure);

    Optional<Failure> findById(Long id);

    List<Failure> findAll();

    List<Failure> findAllByPlantId(Long plantId);

    List<Failure> findAllByAssetId(Long assetId);

    boolean existsByAssetIdAndReportedAt(Long assetId, Instant reportedAt);
}
