package com.techmakers.fixcore.assetmanagement.application.internal.commandservices;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.*;
import com.techmakers.fixcore.assetmanagement.domain.repositories.AssetRepository;
import com.techmakers.fixcore.assetmanagement.domain.repositories.PlantRepository;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetCommandService;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceAlreadyExistsException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class AssetCommandServiceImpl implements AssetCommandService {

    private final AssetRepository assetRepository;
    private final PlantRepository plantRepository;
    private final PlantAccessGuard plantAccessGuard;

    public AssetCommandServiceImpl(AssetRepository assetRepository, PlantRepository plantRepository,
                                   PlantAccessGuard plantAccessGuard) {
        this.assetRepository = assetRepository;
        this.plantRepository = plantRepository;
        this.plantAccessGuard = plantAccessGuard;
    }

    @Override
    public Asset handle(CreateAssetCommand command) {
        if (!plantRepository.existsById(command.plantId())) {
            throw new ResourceNotFoundException("Plant", command.plantId());
        }
        plantAccessGuard.ensureAccessTo(command.plantId());
        if (assetRepository.existsByPlantIdAndCode(command.plantId(), command.details().code())) {
            throw new ResourceAlreadyExistsException("Asset", "code", command.details().code());
        }
        return assetRepository.save(new Asset(command.plantId(), command.details(), Instant.now()));
    }

    @Override
    public Asset handle(UpdateAssetDetailsCommand command) {
        var asset = findOwnedAsset(command.assetId());
        if (assetRepository.existsByPlantIdAndCodeAndIdNot(asset.getPlantId(), command.details().code(), asset.getId())) {
            throw new ResourceAlreadyExistsException("Asset", "code", command.details().code());
        }
        asset.updateDetails(command.details());
        return assetRepository.save(asset);
    }

    @Override
    public void handle(DeleteAssetCommand command) {
        findOwnedAsset(command.assetId());
        assetRepository.deleteById(command.assetId());
    }

    @Override
    public Asset handle(ChangeAssetStatusCommand command) {
        var asset = findOwnedAsset(command.assetId());
        asset.changeStatus(command.status(), Instant.now());
        return assetRepository.save(asset);
    }

    @Override
    public Asset handle(AddAssetManualCommand command) {
        var asset = findOwnedAsset(command.assetId());
        asset.addManual(command.title(), command.url(), Instant.now());
        return assetRepository.save(asset);
    }

    @Override
    public Asset handle(RemoveAssetManualCommand command) {
        var asset = findOwnedAsset(command.assetId());
        asset.removeManual(command.manualIndex());
        return assetRepository.save(asset);
    }

    private Asset findOwnedAsset(Long assetId) {
        var asset = findAsset(assetId);
        plantAccessGuard.ensureAccessTo(asset.getPlantId());
        return asset;
    }

    private Asset findAsset(Long assetId) {
        return assetRepository.findById(assetId).orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
    }
}
