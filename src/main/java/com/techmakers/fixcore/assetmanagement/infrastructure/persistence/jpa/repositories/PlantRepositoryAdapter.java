package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Plant;
import com.techmakers.fixcore.assetmanagement.domain.repositories.PlantRepository;
import com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.mappers.PlantPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlantRepositoryAdapter implements PlantRepository {

    private final PlantJpaRepository plantJpaRepository;

    public PlantRepositoryAdapter(PlantJpaRepository plantJpaRepository) {
        this.plantJpaRepository = plantJpaRepository;
    }

    @Override
    public Plant save(Plant plant) {
        return PlantPersistenceMapper.toDomain(plantJpaRepository.save(PlantPersistenceMapper.toEntity(plant)));
    }

    @Override
    public Optional<Plant> findById(Long id) {
        return plantJpaRepository.findById(id).map(PlantPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Plant> findByName(String name) {
        return plantJpaRepository.findByNameIgnoreCase(name.trim()).map(PlantPersistenceMapper::toDomain);
    }

    @Override
    public List<Plant> findAll() {
        return plantJpaRepository.findAllByOrderByNameAsc().stream().map(PlantPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return plantJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return plantJpaRepository.existsByNameIgnoreCase(name.trim());
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return plantJpaRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), id);
    }

    @Override
    public void deleteById(Long id) {
        plantJpaRepository.deleteById(id);
    }
}
