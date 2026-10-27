package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.entities.FailureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface FailureJpaRepository extends JpaRepository<FailureEntity, Long> {

    List<FailureEntity> findAllByOrderByReportedAtDesc();

    List<FailureEntity> findAllByPlantIdOrderByReportedAtDesc(Long plantId);

    List<FailureEntity> findAllByAssetIdOrderByReportedAtDesc(Long assetId);

    boolean existsByAssetIdAndReportedAt(Long assetId, Instant reportedAt);
}
