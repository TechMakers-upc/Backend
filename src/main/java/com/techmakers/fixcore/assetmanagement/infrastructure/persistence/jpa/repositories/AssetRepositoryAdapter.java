package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.repositories.AssetRepository;
import com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.mappers.AssetPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AssetRepositoryAdapter implements AssetRepository {

    private final AssetJpaRepository assetJpaRepository;

    public AssetRepositoryAdapter(AssetJpaRepository assetJpaRepository) {
        this.assetJpaRepository = assetJpaRepository;
    }

    @Override
    public Asset save(Asset asset) {
        return AssetPersistenceMapper.toDomain(assetJpaRepository.save(AssetPersistenceMapper.toEntity(asset)));
    }

    @Override
    public Optional<Asset> findById(Long id) {
        return assetJpaRepository.findById(id).map(AssetPersistenceMapper::toDomain);
    }

    @Override
    public List<Asset> findAll() {
        return assetJpaRepository.findAllByOrderByCodeAsc().stream().map(AssetPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Asset> findAllByPlantId(Long plantId) {
        return assetJpaRepository.findAllByPlantIdOrderByCodeAsc(plantId).stream()
                .map(AssetPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return assetJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByPlantId(Long plantId) {
        return assetJpaRepository.existsByPlantId(plantId);
    }

    @Override
    public boolean existsByPlantIdAndCode(Long plantId, String code) {
        return assetJpaRepository.existsByPlantIdAndCode(plantId, code);
    }

    @Override
    public boolean existsByPlantIdAndCodeAndIdNot(Long plantId, String code, Long id) {
        return assetJpaRepository.existsByPlantIdAndCodeAndIdNot(plantId, code, id);
    }

    @Override
    public void deleteById(Long id) {
        assetJpaRepository.deleteById(id);
    }
}
