package com.techmakers.fixcore.inventory.domain.model.queries;

public record GetSparePartByIdQuery(Long partId) {

    public GetSparePartByIdQuery {
        if (partId == null || partId <= 0) throw new IllegalArgumentException("Part identifier must be a positive number");
    }
}
