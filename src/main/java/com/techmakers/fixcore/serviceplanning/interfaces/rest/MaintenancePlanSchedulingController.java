package com.techmakers.fixcore.serviceplanning.interfaces.rest;

import com.techmakers.fixcore.serviceplanning.domain.model.commands.ReactivateMaintenancePlanCommand;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.RegisterPlanCompletionCommand;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.RescheduleMaintenancePlanCommand;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.SuspendMaintenancePlanCommand;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanCommandService;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.resources.MaintenancePlanResource;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.resources.RegisterPlanCompletionResource;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.resources.RescheduleMaintenancePlanResource;
import com.techmakers.fixcore.serviceplanning.interfaces.rest.transform.MaintenancePlanResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/maintenance-plans/{planId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Maintenance Plans", description = "Preventive maintenance planning endpoints")
public class MaintenancePlanSchedulingController {

    private final MaintenancePlanCommandService planCommandService;

    public MaintenancePlanSchedulingController(MaintenancePlanCommandService planCommandService) {
        this.planCommandService = planCommandService;
    }

    @PatchMapping("/schedule")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Move the next due date of a plan")
    public ResponseEntity<MaintenancePlanResource> reschedule(@PathVariable Long planId,
                                                              @Valid @RequestBody RescheduleMaintenancePlanResource resource) {
        var plan = planCommandService.handle(new RescheduleMaintenancePlanCommand(planId, resource.nextDueDate()));
        return ResponseEntity.ok(MaintenancePlanResourceAssembler.toResource(plan));
    }

    @PostMapping("/suspension")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Suspend a plan")
    public ResponseEntity<MaintenancePlanResource> suspend(@PathVariable Long planId) {
        var plan = planCommandService.handle(new SuspendMaintenancePlanCommand(planId));
        return ResponseEntity.ok(MaintenancePlanResourceAssembler.toResource(plan));
    }

    @PostMapping("/reactivation")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Reactivate a suspended plan")
    public ResponseEntity<MaintenancePlanResource> reactivate(@PathVariable Long planId) {
        var plan = planCommandService.handle(new ReactivateMaintenancePlanCommand(planId));
        return ResponseEntity.ok(MaintenancePlanResourceAssembler.toResource(plan));
    }

    @PostMapping("/completions")
    @PreAuthorize("hasAnyRole('PLANT_MANAGER', 'TECHNICIAN')")
    @Operation(summary = "Register that the planned maintenance was completed")
    public ResponseEntity<MaintenancePlanResource> registerCompletion(@PathVariable Long planId,
                                                                      @Valid @RequestBody RegisterPlanCompletionResource resource) {
        var plan = planCommandService.handle(new RegisterPlanCompletionCommand(planId, resource.completedOn()));
        return ResponseEntity.ok(MaintenancePlanResourceAssembler.toResource(plan));
    }
}
