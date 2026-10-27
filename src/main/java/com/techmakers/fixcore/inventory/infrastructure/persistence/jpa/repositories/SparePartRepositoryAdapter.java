package com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.repositories.SparePartRepository;
import com.techmakers.fixcore.inventory.infrastructure.persistence.jpa.mappers.SparePartPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SparePartRepositoryAdapter implements SparePartRepository {

    private final SparePartJpaRepository sparePartJpaRepository;

    public SparePartRepositoryAdapter(SparePartJpaRepository sparePartJpaRepository) {
        this.sparePartJpaRepository = sparePartJpaRepository;
    }

    @Override
    public SparePart save(SparePart part) {
        return SparePartPersistenceMapper.toDomain(sparePartJpaRepository.save(SparePartPersistenceMapper.toEntity(part)));
    }

    @Override
    public Optional<SparePart> findById(Long id) {
        return sparePartJpaRepository.findById(id).map(SparePartPersistenceMapper::toDomain);
    }

    @Override
    public List<SparePart> findAll() {
        return sparePartJpaRepository.findAllByOrderByNameAsc().stream().map(SparePartPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<SparePart> findAllByPlantId(Long plantId) {
        return sparePartJpaRepository.findAllByPlantIdOrderByNameAsc(plantId).stream()
                .map(SparePartPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return sparePartJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByPlantIdAndSku(Long plantId, String sku) {
        return sparePartJpaRepository.existsByPlantIdAndSku(plantId, sku);
    }

    @Override
    public boolean existsByPlantIdAndSkuAndIdNot(Long plantId, String sku, Long id) {
        return sparePartJpaRepository.existsByPlantIdAndSkuAndIdNot(plantId, sku, id);
    }

    @Override
    public void deleteById(Long id) {
        sparePartJpaRepository.deleteById(id);
    }
}
