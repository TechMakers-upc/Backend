package com.techmakers.fixcore.inventory.domain.services;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.queries.GetAllSparePartsQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartByIdQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartsBelowMinimumQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartsByPlantIdQuery;

import java.util.List;
import java.util.Optional;

public interface SparePartQueryService {

    List<SparePart> handle(GetAllSparePartsQuery query);

    List<SparePart> handle(GetSparePartsByPlantIdQuery query);

    List<SparePart> handle(GetSparePartsBelowMinimumQuery query);

    Optional<SparePart> handle(GetSparePartByIdQuery query);
}
