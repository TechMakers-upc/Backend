package com.techmakers.fixcore.inventory.interfaces.rest;

import com.techmakers.fixcore.inventory.domain.model.queries.GetStockMovementsQuery;
import com.techmakers.fixcore.inventory.domain.services.StockMovementQueryService;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.StockMovementResource;
import com.techmakers.fixcore.inventory.interfaces.rest.transform.StockMovementResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/stock-movements", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Stock Movements", description = "Spare parts stock history endpoints")
public class StockMovementsController {

    private final StockMovementQueryService movementQueryService;

    public StockMovementsController(StockMovementQueryService movementQueryService) {
        this.movementQueryService = movementQueryService;
    }

    @GetMapping
    @Operation(summary = "List stock movements newest first, optionally by plant or spare part")
    public ResponseEntity<List<StockMovementResource>> getMovements(@RequestParam(required = false) Long plantId,
                                                                    @RequestParam(required = false) Long partId) {
        var movements = movementQueryService.handle(new GetStockMovementsQuery(plantId, partId)).stream()
                .map(StockMovementResourceAssembler::toResource).toList();
        return ResponseEntity.ok(movements);
    }
}
