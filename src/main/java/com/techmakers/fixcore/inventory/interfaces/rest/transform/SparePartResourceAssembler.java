package com.techmakers.fixcore.inventory.interfaces.rest.transform;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.commands.CreateSparePartCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.UpdateSparePartDetailsCommand;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartCategory;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.PartUnit;
import com.techmakers.fixcore.inventory.domain.model.valueobjects.SparePartDetails;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.SaveSparePartResource;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.SparePartResource;

import static com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler.toEnum;
import static com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler.toValue;

public final class SparePartResourceAssembler {

    private SparePartResourceAssembler() {
    }

    public static SparePartResource toResource(SparePart part) {
        var details = part.getDetails();
        return new SparePartResource(part.getId(), part.getPlantId(), details.sku(), details.name(),
                toValue(details.category()), toValue(details.unit()), part.getStock(), details.minStock(),
                details.location(), toValue(part.level()));
    }

    public static CreateSparePartCommand toCreateCommand(SaveSparePartResource resource) {
        if (resource.plantId() == null) throw new IllegalArgumentException("Plant identifier is required");
        return new CreateSparePartCommand(resource.plantId(), toDetails(resource));
    }

    public static UpdateSparePartDetailsCommand toUpdateCommand(Long partId, SaveSparePartResource resource) {
        return new UpdateSparePartDetailsCommand(partId, toDetails(resource));
    }

    private static SparePartDetails toDetails(SaveSparePartResource resource) {
        return new SparePartDetails(resource.sku(), resource.name(), toEnum(PartCategory.class, resource.category()),
                toEnum(PartUnit.class, resource.unit()), resource.minStock(), resource.location());
    }
}
