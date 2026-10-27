package com.techmakers.fixcore.serviceexecution.interfaces.rest;

import com.techmakers.fixcore.serviceexecution.domain.model.commands.ResolveFailureCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailureByIdQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailuresQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureStatus;
import com.techmakers.fixcore.serviceexecution.domain.services.FailureCommandService;
import com.techmakers.fixcore.serviceexecution.domain.services.FailureQueryService;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.FailureResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.PrioritizeFailureResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.ReportFailureResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.transform.FailureResourceAssembler;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import com.techmakers.fixcore.shared.interfaces.rest.transform.EnumValueAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/failures", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Failures", description = "Equipment failure reporting endpoints")
public class FailuresController {

    private final FailureCommandService failureCommandService;
    private final FailureQueryService failureQueryService;

    public FailuresController(FailureCommandService failureCommandService, FailureQueryService failureQueryService) {
        this.failureCommandService = failureCommandService;
        this.failureQueryService = failureQueryService;
    }

    @GetMapping
    @Operation(summary = "List failures newest first, optionally by plant, asset or status")
    public ResponseEntity<List<FailureResource>> getFailures(@RequestParam(required = false) Long plantId,
                                                             @RequestParam(required = false) Long assetId,
                                                             @RequestParam(required = false) String status) {
        var statusFilter = status == null ? null : EnumValueAssembler.toEnum(FailureStatus.class, status);
        var failures = failureQueryService.handle(new GetFailuresQuery(plantId, assetId, statusFilter)).stream()
                .map(FailureResourceAssembler::toResource).toList();
        return ResponseEntity.ok(failures);
    }

    @GetMapping("/{failureId}")
    @Operation(summary = "Get a failure by identifier")
    public ResponseEntity<FailureResource> getFailureById(@PathVariable Long failureId) {
        return failureQueryService.handle(new GetFailureByIdQuery(failureId))
                .map(failure -> ResponseEntity.ok(FailureResourceAssembler.toResource(failure)))
                .orElseThrow(() -> new ResourceNotFoundException("Failure", failureId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'PLANT_MANAGER')")
    @Operation(summary = "Report a failure on an asset")
    public ResponseEntity<FailureResource> reportFailure(@Valid @RequestBody ReportFailureResource resource,
                                                         Principal principal) {
        var failure = failureCommandService.handle(FailureResourceAssembler.toReportCommand(resource, principal.getName()));
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(failure.getId()).toUri();
        return ResponseEntity.created(location).body(FailureResourceAssembler.toResource(failure));
    }

    @PatchMapping("/{failureId}/priority")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Set the priority of a failure")
    public ResponseEntity<FailureResource> prioritize(@PathVariable Long failureId,
                                                      @Valid @RequestBody PrioritizeFailureResource resource) {
        var failure = failureCommandService.handle(
                FailureResourceAssembler.toPrioritizeCommand(failureId, resource.priority()));
        return ResponseEntity.ok(FailureResourceAssembler.toResource(failure));
    }

    @PostMapping("/{failureId}/resolution")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Mark a failure as resolved")
    public ResponseEntity<FailureResource> resolve(@PathVariable Long failureId) {
        var failure = failureCommandService.handle(new ResolveFailureCommand(failureId));
        return ResponseEntity.ok(FailureResourceAssembler.toResource(failure));
    }
}
