package com.techmakers.fixcore.inventory.application.internal.commandservices;

import com.techmakers.fixcore.inventory.application.outboundservices.acl.PlantCatalog;
import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.commands.CreateSparePartCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.DeleteSparePartCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.UpdateSparePartDetailsCommand;
import com.techmakers.fixcore.inventory.domain.repositories.SparePartRepository;
import com.techmakers.fixcore.inventory.domain.services.SparePartCommandService;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceAlreadyExistsException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SparePartCommandServiceImpl implements SparePartCommandService {

    private final SparePartRepository partRepository;
    private final PlantCatalog plantCatalog;

    public SparePartCommandServiceImpl(SparePartRepository partRepository, PlantCatalog plantCatalog) {
        this.partRepository = partRepository;
        this.plantCatalog = plantCatalog;
    }

    @Override
    public SparePart handle(CreateSparePartCommand command) {
        if (!plantCatalog.existsPlant(command.plantId())) {
            throw new ResourceNotFoundException("Plant", command.plantId());
        }
        if (partRepository.existsByPlantIdAndSku(command.plantId(), command.details().sku())) {
            throw new ResourceAlreadyExistsException("SparePart", "sku", command.details().sku());
        }
        return partRepository.save(new SparePart(command.plantId(), command.details()));
    }

    @Override
    public SparePart handle(UpdateSparePartDetailsCommand command) {
        var part = partRepository.findById(command.partId())
                .orElseThrow(() -> new ResourceNotFoundException("SparePart", command.partId()));
        if (partRepository.existsByPlantIdAndSkuAndIdNot(part.getPlantId(), command.details().sku(), part.getId())) {
            throw new ResourceAlreadyExistsException("SparePart", "sku", command.details().sku());
        }
        part.updateDetails(command.details());
        return partRepository.save(part);
    }

    @Override
    public void handle(DeleteSparePartCommand command) {
        if (!partRepository.existsById(command.partId())) {
            throw new ResourceNotFoundException("SparePart", command.partId());
        }
        partRepository.deleteById(command.partId());
    }
}
