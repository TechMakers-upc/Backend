package com.techmakers.fixcore.serviceexecution.interfaces.rest;

import com.techmakers.fixcore.serviceexecution.domain.model.commands.AssignWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.DeleteWorkOrderCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetWorkOrderByIdQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetWorkOrdersQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.WorkOrderStatus;
import com.techmakers.fixcore.serviceexecution.domain.services.WorkOrderCommandService;
import com.techmakers.fixcore.serviceexecution.domain.services.WorkOrderQueryService;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.CreateWorkOrderResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.UpdateWorkOrderResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderActionResources.AssignTechnicianResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.resources.WorkOrderResource;
import com.techmakers.fixcore.serviceexecution.interfaces.rest.transform.WorkOrderResourceAssembler;
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
@RequestMapping(value = "/api/v1/work-orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Work Orders", description = "Maintenance work order endpoints")
public class WorkOrdersController {

    private static final String MANAGERS = "hasAnyRole('PLANT_MANAGER', 'OPERATIONS_MANAGER')";

    private final WorkOrderCommandService workOrderCommandService;
    private final WorkOrderQueryService workOrderQueryService;

    public WorkOrdersController(WorkOrderCommandService workOrderCommandService,
                                WorkOrderQueryService workOrderQueryService) {
        this.workOrderCommandService = workOrderCommandService;
        this.workOrderQueryService = workOrderQueryService;
    }

    @GetMapping
    @Operation(summary = "List work orders: open first, then by priority and due date")
    public ResponseEntity<List<WorkOrderResource>> getWorkOrders(@RequestParam(required = false) Long plantId,
                                                                 @RequestParam(required = false) Long assetId,
                                                                 @RequestParam(required = false) Long technicianId,
                                                                 @RequestParam(required = false) String status) {
        var statusFilter = status == null ? null : EnumValueAssembler.toEnum(WorkOrderStatus.class, status);
        var orders = workOrderQueryService.handle(new GetWorkOrdersQuery(plantId, assetId, technicianId, statusFilter));
        return ResponseEntity.ok(orders.stream().map(WorkOrderResourceAssembler::toResource).toList());
    }

    @GetMapping("/{workOrderId}")
    @Operation(summary = "Get a work order by identifier")
    public ResponseEntity<WorkOrderResource> getWorkOrderById(@PathVariable Long workOrderId) {
        return workOrderQueryService.handle(new GetWorkOrderByIdQuery(workOrderId))
                .map(order -> ResponseEntity.ok(WorkOrderResourceAssembler.toResource(order)))
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", workOrderId));
    }

    @PostMapping
    @PreAuthorize(MANAGERS)
    @Operation(summary = "Create a work order, optionally from a failure or a preventive plan")
    public ResponseEntity<WorkOrderResource> createWorkOrder(@Valid @RequestBody CreateWorkOrderResource resource,
                                                             Principal principal) {
        var order = workOrderCommandService.handle(WorkOrderResourceAssembler.toCreateCommand(resource, principal.getName()));
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).body(WorkOrderResourceAssembler.toResource(order));
    }

    @PutMapping("/{workOrderId}")
    @PreAuthorize(MANAGERS)
    @Operation(summary = "Update the details of an open work order")
    public ResponseEntity<WorkOrderResource> updateWorkOrder(@PathVariable Long workOrderId,
                                                             @Valid @RequestBody UpdateWorkOrderResource resource) {
        var order = workOrderCommandService.handle(WorkOrderResourceAssembler.toUpdateCommand(workOrderId, resource));
        return ResponseEntity.ok(WorkOrderResourceAssembler.toResource(order));
    }

    @PatchMapping("/{workOrderId}/assignment")
    @PreAuthorize(MANAGERS)
    @Operation(summary = "Assign a technician to a work order")
    public ResponseEntity<WorkOrderResource> assign(@PathVariable Long workOrderId,
                                                    @Valid @RequestBody AssignTechnicianResource resource) {
        var order = workOrderCommandService.handle(new AssignWorkOrderCommand(workOrderId, resource.technicianId()));
        return ResponseEntity.ok(WorkOrderResourceAssembler.toResource(order));
    }

    @DeleteMapping("/{workOrderId}")
    @PreAuthorize(MANAGERS)
    @Operation(summary = "Delete a pending work order that does not come from a failure")
    public ResponseEntity<Void> deleteWorkOrder(@PathVariable Long workOrderId) {
        workOrderCommandService.handle(new DeleteWorkOrderCommand(workOrderId));
        return ResponseEntity.noContent().build();
    }
}
