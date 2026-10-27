package com.techmakers.fixcore.analytics.domain.model.queries;

import com.techmakers.fixcore.analytics.domain.model.valueobjects.Period;

public record GetTechnicianPerformanceQuery(Long technicianId, Period period) {

    public GetTechnicianPerformanceQuery {
        if (technicianId == null) throw new IllegalArgumentException("Technician is required");
    }
}
