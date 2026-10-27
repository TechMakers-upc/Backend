package com.techmakers.fixcore.analytics.domain.model.queries;

import com.techmakers.fixcore.analytics.domain.model.valueobjects.Period;

public record GetMaintenanceSummaryQuery(Long plantId, Period period) {
}
