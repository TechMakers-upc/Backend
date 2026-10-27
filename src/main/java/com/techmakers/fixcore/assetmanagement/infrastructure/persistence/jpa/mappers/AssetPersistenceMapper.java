package com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.mappers;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetDetails;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.Manual;
import com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.entities.AssetEntity;
import com.techmakers.fixcore.assetmanagement.infrastructure.persistence.jpa.entities.ManualEmbeddable;

public final class AssetPersistenceMapper {

    private AssetPersistenceMapper() {
    }

    public static AssetEntity toEntity(Asset asset) {
        var details = asset.getDetails();
        var entity = new AssetEntity(asset.getPlantId(), asset.getStatus(), asset.getStatusChangedAt());
        entity.setId(asset.getId());
        entity.setDetails(details.code(), details.name(), details.category(), details.criticality(), details.location(),
                details.brand(), details.model(), details.serialNumber(), details.installedAt(), details.description());
        entity.setManuals(asset.getManuals().stream()
                .map(manual -> new ManualEmbeddable(manual.title(), manual.url(), manual.addedAt()))
                .toList());
        return entity;
    }

    public static Asset toDomain(AssetEntity entity) {
        var details = new AssetDetails(entity.getCode(), entity.getName(), entity.getCategory(), entity.getCriticality(),
                entity.getLocation(), entity.getBrand(), entity.getModel(), entity.getSerialNumber(),
                entity.getInstalledAt(), entity.getDescription());
        var manuals = entity.getManuals().stream()
                .map(manual -> new Manual(manual.getTitle(), manual.getUrl(), manual.getAddedAt()))
                .toList();
        return new Asset(entity.getId(), entity.getPlantId(), details, entity.getStatus(),
                entity.getStatusChangedAt(), manuals);
    }
}
