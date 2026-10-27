package com.techmakers.fixcore.inventory.application.internal.queryservices;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.queries.GetAllSparePartsQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartByIdQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartsBelowMinimumQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartsByPlantIdQuery;
import com.techmakers.fixcore.inventory.domain.repositories.SparePartRepository;
import com.techmakers.fixcore.inventory.domain.services.SparePartQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SparePartQueryServiceImpl implements SparePartQueryService {

    private final SparePartRepository partRepository;

    public SparePartQueryServiceImpl(SparePartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    public List<SparePart> handle(GetAllSparePartsQuery query) {
        return partRepository.findAll();
    }

    @Override
    public List<SparePart> handle(GetSparePartsByPlantIdQuery query) {
        return partRepository.findAllByPlantId(query.plantId());
    }

    @Override
    public List<SparePart> handle(GetSparePartsBelowMinimumQuery query) {
        var parts = query.plantId() == null ? partRepository.findAll() : partRepository.findAllByPlantId(query.plantId());
        return parts.stream().filter(SparePart::isBelowMinimum).toList();
    }

    @Override
    public Optional<SparePart> handle(GetSparePartByIdQuery query) {
        return partRepository.findById(query.partId());
    }
}
