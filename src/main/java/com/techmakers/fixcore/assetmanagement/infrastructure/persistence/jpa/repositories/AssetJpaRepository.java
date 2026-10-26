package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.entities.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetJpaRepository extends JpaRepository<AssetEntity, Long> {

    List<AssetEntity> findAllByOrderByCodeAsc();

    List<AssetEntity> findAllByPlantIdOrderByCodeAsc(Long plantId);

    boolean existsByPlantId(Long plantId);

    boolean existsByPlantIdAndCode(Long plantId, String code);

    boolean existsByPlantIdAndCodeAndIdNot(Long plantId, String code, Long id);
}
