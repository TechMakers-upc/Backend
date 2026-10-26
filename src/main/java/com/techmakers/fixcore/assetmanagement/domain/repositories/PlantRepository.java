package com.techmakers.fixcore.assetmanagement.domain.repositories;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;

import java.util.List;
import java.util.Optional;

public interface PlantRepository {

    Plant save(Plant plant);

    Optional<Plant> findById(Long id);

    List<Plant> findAll();

    boolean existsById(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    void deleteById(Long id);
}
