package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.entities.PlantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantJpaRepository extends JpaRepository<PlantEntity, Long> {

    List<PlantEntity> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
