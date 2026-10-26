package com.techmakers.fixcore.assetmanagement.interfaces.rest;

import com.techmakers.fixcore.assetmanagement.domain.model.commands.DeletePlantCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllPlantsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetPlantByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.services.PlantCommandService;
import com.techmakers.fixcore.assetmanagement.domain.services.PlantQueryService;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.PlantResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.SavePlantResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.transform.PlantResourceAssembler;
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
@RequestMapping(value = "/api/v1/plants", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Plants", description = "Industrial plant management endpoints")
public class PlantsController {

    private final PlantCommandService plantCommandService;
    private final PlantQueryService plantQueryService;

    public PlantsController(PlantCommandService plantCommandService, PlantQueryService plantQueryService) {
        this.plantCommandService = plantCommandService;
        this.plantQueryService = plantQueryService;
    }

    @GetMapping
    @Operation(summary = "List all plants ordered by name")
    public ResponseEntity<List<PlantResource>> getAllPlants() {
        var plants = plantQueryService.handle(new GetAllPlantsQuery()).stream()
                .map(PlantResourceAssembler::toResource).toList();
        return ResponseEntity.ok(plants);
    }

    @GetMapping("/{plantId}")
    @Operation(summary = "Get a plant by identifier")
    public ResponseEntity<PlantResource> getPlantById(@PathVariable Long plantId) {
        return plantQueryService.handle(new GetPlantByIdQuery(plantId))
                .map(plant -> ResponseEntity.ok(PlantResourceAssembler.toResource(plant)))
                .orElseThrow(() -> new ResourceNotFoundException("Plant", plantId));
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERATIONS_MANAGER')")
    @Operation(summary = "Register a new plant")
    public ResponseEntity<PlantResource> createPlant(@Valid @RequestBody SavePlantResource resource) {
        var plant = plantCommandService.handle(PlantResourceAssembler.toCreateCommand(resource));
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(plant.getId()).toUri();
        return ResponseEntity.created(location).body(PlantResourceAssembler.toResource(plant));
    }

    @PutMapping("/{plantId}")
    @PreAuthorize("hasRole('OPERATIONS_MANAGER')")
    @Operation(summary = "Update the details of a plant")
    public ResponseEntity<PlantResource> updatePlant(@PathVariable Long plantId,
                                                     @Valid @RequestBody SavePlantResource resource) {
        var plant = plantCommandService.handle(PlantResourceAssembler.toUpdateCommand(plantId, resource));
        return ResponseEntity.ok(PlantResourceAssembler.toResource(plant));
    }

    @DeleteMapping("/{plantId}")
    @PreAuthorize("hasRole('OPERATIONS_MANAGER')")
    @Operation(summary = "Delete a plant")
    public ResponseEntity<Void> deletePlant(@PathVariable Long plantId) {
        plantCommandService.handle(new DeletePlantCommand(plantId));
        return ResponseEntity.noContent().build();
    }
}
