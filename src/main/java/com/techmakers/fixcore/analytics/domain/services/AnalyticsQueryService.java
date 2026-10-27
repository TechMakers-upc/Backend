package com.techmakers.fixcore.analytics.domain.services;

import com.techmakers.fixcore.analytics.domain.model.queries.GetDowntimeRankingQuery;
import com.techmakers.fixcore.analytics.domain.model.queries.GetMaintenanceSummaryQuery;
import com.techmakers.fixcore.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.techmakers.fixcore.analytics.domain.model.valueobjects.DowntimeEntry;
import com.techmakers.fixcore.analytics.domain.model.valueobjects.MaintenanceSummary;
import com.techmakers.fixcore.analytics.domain.model.valueobjects.TechnicianPerformance;

import java.util.List;

public interface AnalyticsQueryService {

    MaintenanceSummary handle(GetMaintenanceSummaryQuery query);

    List<DowntimeEntry> handle(GetDowntimeRankingQuery query);

    TechnicianPerformance handle(GetTechnicianPerformanceQuery query);
}
