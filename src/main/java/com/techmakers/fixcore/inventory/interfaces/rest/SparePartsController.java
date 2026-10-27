package com.techmakers.fixcore.inventory.interfaces.rest;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.commands.DeleteSparePartCommand;
import com.techmakers.fixcore.inventory.domain.model.queries.GetAllSparePartsQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartByIdQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartsBelowMinimumQuery;
import com.techmakers.fixcore.inventory.domain.model.queries.GetSparePartsByPlantIdQuery;
import com.techmakers.fixcore.inventory.domain.services.SparePartCommandService;
import com.techmakers.fixcore.inventory.domain.services.SparePartQueryService;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.SaveSparePartResource;
import com.techmakers.fixcore.inventory.interfaces.rest.resources.SparePartResource;
import com.techmakers.fixcore.inventory.interfaces.rest.transform.SparePartResourceAssembler;
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
@RequestMapping(value = "/api/v1/spare-parts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Spare Parts", description = "Spare parts inventory endpoints")
public class SparePartsController {

    private final SparePartCommandService partCommandService;
    private final SparePartQueryService partQueryService;

    public SparePartsController(SparePartCommandService partCommandService, SparePartQueryService partQueryService) {
        this.partCommandService = partCommandService;
        this.partQueryService = partQueryService;
    }

    @GetMapping
    @Operation(summary = "List spare parts by name, optionally by plant or only those below their minimum")
    public ResponseEntity<List<SparePartResource>> getSpareParts(@RequestParam(required = false) Long plantId,
                                                                 @RequestParam(defaultValue = "false") boolean belowMinimum) {
        List<SparePart> parts;
        if (belowMinimum) parts = partQueryService.handle(new GetSparePartsBelowMinimumQuery(plantId));
        else if (plantId != null) parts = partQueryService.handle(new GetSparePartsByPlantIdQuery(plantId));
        else parts = partQueryService.handle(new GetAllSparePartsQuery());
        return ResponseEntity.ok(parts.stream().map(SparePartResourceAssembler::toResource).toList());
    }

    @GetMapping("/{partId}")
    @Operation(summary = "Get a spare part by identifier")
    public ResponseEntity<SparePartResource> getSparePartById(@PathVariable Long partId) {
        return partQueryService.handle(new GetSparePartByIdQuery(partId))
                .map(part -> ResponseEntity.ok(SparePartResourceAssembler.toResource(part)))
                .orElseThrow(() -> new ResourceNotFoundException("SparePart", partId));
    }

    @PostMapping
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Register a spare part in a plant inventory")
    public ResponseEntity<SparePartResource> createSparePart(@Valid @RequestBody SaveSparePartResource resource) {
        var part = partCommandService.handle(SparePartResourceAssembler.toCreateCommand(resource));
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(part.getId()).toUri();
        return ResponseEntity.created(location).body(SparePartResourceAssembler.toResource(part));
    }

    @PutMapping("/{partId}")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Update the details of a spare part")
    public ResponseEntity<SparePartResource> updateSparePart(@PathVariable Long partId,
                                                             @Valid @RequestBody SaveSparePartResource resource) {
        var part = partCommandService.handle(SparePartResourceAssembler.toUpdateCommand(partId, resource));
        return ResponseEntity.ok(SparePartResourceAssembler.toResource(part));
    }

    @DeleteMapping("/{partId}")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Delete a spare part")
    public ResponseEntity<Void> deleteSparePart(@PathVariable Long partId) {
        partCommandService.handle(new DeleteSparePartCommand(partId));
        return ResponseEntity.noContent().build();
    }
}
