package com.techmakers.fixcore.assetmanagement.interfaces.rest;

import com.techmakers.fixcore.assetmanagement.domain.model.commands.DeleteAssetCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAllAssetsQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetByIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.model.queries.GetAssetsByPlantIdQuery;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetCommandService;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetQueryService;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.AssetResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.SaveAssetResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.transform.AssetResourceAssembler;
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
@RequestMapping(value = "/api/v1/assets", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Assets", description = "Machine and equipment management endpoints")
public class AssetsController {

    private final AssetCommandService assetCommandService;
    private final AssetQueryService assetQueryService;

    public AssetsController(AssetCommandService assetCommandService, AssetQueryService assetQueryService) {
        this.assetCommandService = assetCommandService;
        this.assetQueryService = assetQueryService;
    }

    @GetMapping
    @Operation(summary = "List assets, optionally filtered by plant")
    public ResponseEntity<List<AssetResource>> getAssets(@RequestParam(required = false) Long plantId) {
        var assets = plantId == null
                ? assetQueryService.handle(new GetAllAssetsQuery())
                : assetQueryService.handle(new GetAssetsByPlantIdQuery(plantId));
        return ResponseEntity.ok(assets.stream().map(AssetResourceAssembler::toResource).toList());
    }

    @GetMapping("/{assetId}")
    @Operation(summary = "Get an asset by identifier")
    public ResponseEntity<AssetResource> getAssetById(@PathVariable Long assetId) {
        return assetQueryService.handle(new GetAssetByIdQuery(assetId))
                .map(asset -> ResponseEntity.ok(AssetResourceAssembler.toResource(asset)))
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
    }

    @PostMapping
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Register a new asset in a plant")
    public ResponseEntity<AssetResource> createAsset(@Valid @RequestBody SaveAssetResource resource) {
        var asset = assetCommandService.handle(AssetResourceAssembler.toCreateCommand(resource));
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(asset.getId()).toUri();
        return ResponseEntity.created(location).body(AssetResourceAssembler.toResource(asset));
    }

    @PutMapping("/{assetId}")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Update the details of an asset")
    public ResponseEntity<AssetResource> updateAsset(@PathVariable Long assetId,
                                                     @Valid @RequestBody SaveAssetResource resource) {
        var asset = assetCommandService.handle(AssetResourceAssembler.toUpdateCommand(assetId, resource));
        return ResponseEntity.ok(AssetResourceAssembler.toResource(asset));
    }

    @DeleteMapping("/{assetId}")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Delete an asset")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long assetId) {
        assetCommandService.handle(new DeleteAssetCommand(assetId));
        return ResponseEntity.noContent().build();
    }
}
