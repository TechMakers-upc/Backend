package com.techmakers.fixcore.assetmanagement.interfaces.rest.transform;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.ChangeAssetStatusCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.CreateAssetCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.UpdateAssetDetailsCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetCategory;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetDetails;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.AssetStatus;
import com.techmakers.fixcore.assetmanagement.domain.model.valueobjects.Criticality;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.AssetResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.ManualResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.SaveAssetResource;

import static com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler.toEnum;
import static com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler.toValue;

public final class AssetResourceAssembler {

    private AssetResourceAssembler() {
    }

    public static AssetResource toResource(Asset asset) {
        var details = asset.getDetails();
        var manuals = asset.getManuals().stream()
                .map(manual -> new ManualResource(manual.title(), manual.url(), manual.addedAt())).toList();
        return new AssetResource(asset.getId(), asset.getPlantId(), details.code(), details.name(),
                toValue(details.category()), toValue(details.criticality()), toValue(asset.getStatus()),
                asset.getStatusChangedAt(), details.location(), details.brand(), details.model(),
                details.serialNumber(), details.installedAt(), details.description(), manuals);
    }

    public static CreateAssetCommand toCreateCommand(SaveAssetResource resource) {
        if (resource.plantId() == null) throw new IllegalArgumentException("Plant identifier is required");
        return new CreateAssetCommand(resource.plantId(), toDetails(resource));
    }

    public static UpdateAssetDetailsCommand toUpdateCommand(Long assetId, SaveAssetResource resource) {
        return new UpdateAssetDetailsCommand(assetId, toDetails(resource));
    }

    public static ChangeAssetStatusCommand toStatusCommand(Long assetId, String status) {
        return new ChangeAssetStatusCommand(assetId, toEnum(AssetStatus.class, status));
    }

    private static AssetDetails toDetails(SaveAssetResource resource) {
        return new AssetDetails(resource.code(), resource.name(), toEnum(AssetCategory.class, resource.category()),
                toEnum(Criticality.class, resource.criticality()), resource.location(), resource.brand(),
                resource.model(), resource.serialNumber(), resource.installedAt(), resource.description());
    }
}
