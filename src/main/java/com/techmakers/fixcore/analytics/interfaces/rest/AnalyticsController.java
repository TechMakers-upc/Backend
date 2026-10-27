package com.techmakers.fixcore.analytics.interfaces.rest;

import com.techmakers.fixcore.analytics.domain.model.queries.GetDowntimeRankingQuery;
import com.techmakers.fixcore.analytics.domain.model.queries.GetMaintenanceSummaryQuery;
import com.techmakers.fixcore.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.techmakers.fixcore.analytics.domain.model.valueobjects.Period;
import com.techmakers.fixcore.analytics.domain.services.AnalyticsQueryService;
import com.techmakers.fixcore.analytics.interfaces.rest.resources.DowntimeEntryResource;
import com.techmakers.fixcore.analytics.interfaces.rest.resources.MaintenanceSummaryResource;
import com.techmakers.fixcore.analytics.interfaces.rest.resources.TechnicianPerformanceResource;
import com.techmakers.fixcore.analytics.interfaces.rest.transform.AnalyticsResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyRole('PLANT_MANAGER', 'OPERATIONS_MANAGER')")
@Tag(name = "Analytics", description = "Maintenance dashboard and report endpoints")
public class AnalyticsController {

    private final AnalyticsQueryService analyticsQueryService;

    public AnalyticsController(AnalyticsQueryService analyticsQueryService) {
        this.analyticsQueryService = analyticsQueryService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get maintenance KPIs for a plant or for every plant in scope")
    public ResponseEntity<MaintenanceSummaryResource> getSummary(@RequestParam(required = false) Long plantId,
                                                                 @RequestParam(required = false) LocalDate from,
                                                                 @RequestParam(required = false) LocalDate to) {
        var summary = analyticsQueryService.handle(new GetMaintenanceSummaryQuery(plantId, new Period(from, to)));
        return ResponseEntity.ok(AnalyticsResourceAssembler.toResource(summary));
    }

    @GetMapping("/downtime-ranking")
    @Operation(summary = "Rank machines by hours lost to stopped lines")
    public ResponseEntity<List<DowntimeEntryResource>> getDowntimeRanking(@RequestParam(required = false) Long plantId,
                                                                          @RequestParam(required = false) LocalDate from,
                                                                          @RequestParam(required = false) LocalDate to) {
        var ranking = analyticsQueryService.handle(new GetDowntimeRankingQuery(plantId, new Period(from, to)));
        return ResponseEntity.ok(ranking.stream().map(AnalyticsResourceAssembler::toResource).toList());
    }

    @GetMapping("/technicians/{technicianId}/performance")
    @Operation(summary = "Get the workload and repair times of a technician")
    public ResponseEntity<TechnicianPerformanceResource> getTechnicianPerformance(
            @PathVariable Long technicianId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        var performance = analyticsQueryService.handle(new GetTechnicianPerformanceQuery(technicianId, new Period(from, to)));
        return ResponseEntity.ok(AnalyticsResourceAssembler.toResource(performance));
    }
}
