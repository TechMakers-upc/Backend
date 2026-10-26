package com.techmakers.fixcore.serviceplanning.interfaces.rest;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.DeleteMaintenancePlanCommand;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.*;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanCommandService;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanQueryService;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.resources.MaintenancePlanResource;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.resources.SaveMaintenancePlanResource;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.transform.MaintenancePlanResourceAssembler;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/maintenance-plans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Maintenance Plans", description = "Preventive maintenance planning endpoints")
public class MaintenancePlansController {

    private final MaintenancePlanCommandService planCommandService;
    private final MaintenancePlanQueryService planQueryService;

    public MaintenancePlansController(MaintenancePlanCommandService planCommandService,
                                      MaintenancePlanQueryService planQueryService) {
        this.planCommandService = planCommandService;
        this.planQueryService = planQueryService;
    }

    @GetMapping
    @Operation(summary = "List maintenance plans by due date, optionally filtered by plant or asset")
    public ResponseEntity<List<MaintenancePlanResource>> getPlans(@RequestParam(required = false) Long plantId,
                                                                  @RequestParam(required = false) Long assetId) {
        List<MaintenancePlan> plans;
        if (assetId != null) plans = planQueryService.handle(new GetMaintenancePlansByAssetIdQuery(assetId));
        else if (plantId != null) plans = planQueryService.handle(new GetMaintenancePlansByPlantIdQuery(plantId));
        else plans = planQueryService.handle(new GetAllMaintenancePlansQuery());
        return ResponseEntity.ok(plans.stream().map(MaintenancePlanResourceAssembler::toResource).toList());
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get a maintenance plan by identifier")
    public ResponseEntity<MaintenancePlanResource> getPlanById(@PathVariable Long planId) {
        return planQueryService.handle(new GetMaintenancePlanByIdQuery(planId))
                .map(plan -> ResponseEntity.ok(MaintenancePlanResourceAssembler.toResource(plan)))
                .orElseThrow(() -> new ResourceNotFoundException("MaintenancePlan", planId));
    }

    @PostMapping
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Schedule a new preventive maintenance plan")
    public ResponseEntity<MaintenancePlanResource> createPlan(@Valid @RequestBody SaveMaintenancePlanResource resource) {
        var plan = planCommandService.handle(MaintenancePlanResourceAssembler.toCreateCommand(resource));
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(plan.getId()).toUri();
        return ResponseEntity.created(location).body(MaintenancePlanResourceAssembler.toResource(plan));
    }

    @PutMapping("/{planId}")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Update a maintenance plan")
    public ResponseEntity<MaintenancePlanResource> updatePlan(@PathVariable Long planId,
                                                              @Valid @RequestBody SaveMaintenancePlanResource resource) {
        var plan = planCommandService.handle(MaintenancePlanResourceAssembler.toUpdateCommand(planId, resource));
        return ResponseEntity.ok(MaintenancePlanResourceAssembler.toResource(plan));
    }

    @DeleteMapping("/{planId}")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Delete a maintenance plan")
    public ResponseEntity<Void> deletePlan(@PathVariable Long planId) {
        planCommandService.handle(new DeleteMaintenancePlanCommand(planId));
        return ResponseEntity.noContent().build();
    }
}
