package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.repositories.FailureRepository;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.FailureEntity;
import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.mappers.FailurePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class FailureRepositoryAdapter implements FailureRepository {

    private final FailureJpaRepository failureJpaRepository;

    public FailureRepositoryAdapter(FailureJpaRepository failureJpaRepository) {
        this.failureJpaRepository = failureJpaRepository;
    }

    @Override
    public Failure save(Failure failure) {
        return FailurePersistenceMapper.toDomain(failureJpaRepository.save(FailurePersistenceMapper.toEntity(failure)));
    }

    @Override
    public Optional<Failure> findById(Long id) {
        return failureJpaRepository.findById(id).map(FailurePersistenceMapper::toDomain);
    }

    @Override
    public List<Failure> findAll() {
        return toDomain(failureJpaRepository.findAllByOrderByReportedAtDesc());
    }

    @Override
    public List<Failure> findAllByPlantId(Long plantId) {
        return toDomain(failureJpaRepository.findAllByPlantIdOrderByReportedAtDesc(plantId));
    }

    @Override
    public List<Failure> findAllByAssetId(Long assetId) {
        return toDomain(failureJpaRepository.findAllByAssetIdOrderByReportedAtDesc(assetId));
    }

    @Override
    public boolean existsByAssetIdAndReportedAt(Long assetId, Instant reportedAt) {
        return failureJpaRepository.existsByAssetIdAndReportedAt(assetId, reportedAt);
    }

    private List<Failure> toDomain(List<FailureEntity> entities) {
        return entities.stream().map(FailurePersistenceMapper::toDomain).toList();
    }
}
