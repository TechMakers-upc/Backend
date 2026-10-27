package com.techmakers.fixcore.serviceexecution.interfaces.rest;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.CompleteWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.LogWorkCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.RegisterPartUsageCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.StartWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.services.WorkOrderExecutionService;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderActionResources.CompleteWorkOrderResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderActionResources.LogWorkResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderActionResources.RegisterPartUsageResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.transform.WorkOrderResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/v1/work-orders/{workOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyRole('TECHNICIAN', 'PLANT_MANAGER')")
@Tag(name = "Work Orders", description = "Maintenance work order endpoints")
public class WorkOrderExecutionController {

    private final WorkOrderExecutionService executionService;

    public WorkOrderExecutionController(WorkOrderExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/start")
    @Operation(summary = "Start the repair clock of a pending work order")
    public ResponseEntity<WorkOrderResource> start(@PathVariable Long workOrderId) {
        return ok(executionService.handle(new StartWorkOrderCommand(workOrderId)));
    }

    @PostMapping("/work-logs")
    @Operation(summary = "Add a note to the work log of an order in progress")
    public ResponseEntity<WorkOrderResource> logWork(@PathVariable Long workOrderId,
                                                     @Valid @RequestBody LogWorkResource resource, Principal principal) {
        return ok(executionService.handle(new LogWorkCommand(workOrderId, resource.note(), principal.getName())));
    }

    @PostMapping("/part-usages")
    @Operation(summary = "Record a spare part used in an order in progress")
    public ResponseEntity<WorkOrderResource> registerPartUsage(@PathVariable Long workOrderId,
                                                               @Valid @RequestBody RegisterPartUsageResource resource,
                                                               Principal principal) {
        return ok(executionService.handle(new RegisterPartUsageCommand(workOrderId, resource.partId(),
                resource.quantity(), principal.getName())));
    }

    @PostMapping("/completion")
    @Operation(summary = "Close a work order with the solution applied")
    public ResponseEntity<WorkOrderResource> complete(@PathVariable Long workOrderId,
                                                      @Valid @RequestBody CompleteWorkOrderResource resource) {
        return ok(executionService.handle(new CompleteWorkOrderCommand(workOrderId, resource.solution())));
    }

    private ResponseEntity<WorkOrderResource> ok(WorkOrder order) {
        return ResponseEntity.ok(WorkOrderResourceAssembler.toResource(order));
    }
}
