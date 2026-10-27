package com.techmakers.fixcore.inventory.interfaces.rest;

import com.techmakers.fixcore.inventory.domain.model.commands.AdjustStockCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.ConsumeStockCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.ReceiveStockCommand;
import com.techmakers.fixcore.inventory.domain.services.StockCommandService;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.AdjustStockResource;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.ConsumeStockResource;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.ReceiveStockResource;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.SparePartResource;
import com.techmakers.fixcore.inventory.interfaces.rest.transform.SparePartResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/v1/spare-parts/{partId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Spare Parts", description = "Spare parts inventory endpoints")
public class StockOperationsController {

    private final StockCommandService stockCommandService;

    public StockOperationsController(StockCommandService stockCommandService) {
        this.stockCommandService = stockCommandService;
    }

    @PostMapping("/entries")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Register received units of a spare part")
    public ResponseEntity<SparePartResource> receive(@PathVariable Long partId,
                                                     @Valid @RequestBody ReceiveStockResource resource,
                                                     Principal principal) {
        var part = stockCommandService.handle(new ReceiveStockCommand(partId, resource.quantity(), resource.reason(),
                principal.getName()));
        return ResponseEntity.ok(SparePartResourceAssembler.toResource(part));
    }

    @PostMapping("/adjustments")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Adjust the stock of a spare part to a physical count")
    public ResponseEntity<SparePartResource> adjust(@PathVariable Long partId,
                                                    @Valid @RequestBody AdjustStockResource resource,
                                                    Principal principal) {
        var part = stockCommandService.handle(new AdjustStockCommand(partId, resource.countedStock(), resource.reason(),
                principal.getName()));
        return ResponseEntity.ok(SparePartResourceAssembler.toResource(part));
    }

    @PostMapping("/consumptions")
    @PreAuthorize("hasAnyRole('PLANT_MANAGER', 'TECHNICIAN')")
    @Operation(summary = "Consume units of a spare part, optionally for a work order")
    public ResponseEntity<SparePartResource> consume(@PathVariable Long partId,
                                                     @Valid @RequestBody ConsumeStockResource resource,
                                                     Principal principal) {
        var part = stockCommandService.handle(new ConsumeStockCommand(partId, resource.quantity(),
                resource.workOrderId(), resource.reason(), principal.getName()));
        return ResponseEntity.ok(SparePartResourceAssembler.toResource(part));
    }
}
