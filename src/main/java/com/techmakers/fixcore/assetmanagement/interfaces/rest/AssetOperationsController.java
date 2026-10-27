package com.techmakers.fixcore.assetmanagement.interfaces.rest;

import com.techmakers.fixcore.assetmanagement.domain.model.commands.AddAssetManualCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.RemoveAssetManualCommand;
import com.techmakers.fixcore.assetmanagement.domain.services.AssetCommandService;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.AddManualResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.AssetResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.resources.ChangeAssetStatusResource;
import com.techmakers.fixcore.assetmanagement.interfaces.rest.transform.AssetResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/assets/{assetId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Assets", description = "Machine and equipment management endpoints")
public class AssetOperationsController {

    private final AssetCommandService assetCommandService;

    public AssetOperationsController(AssetCommandService assetCommandService) {
        this.assetCommandService = assetCommandService;
    }

    @PatchMapping("/status")
    @PreAuthorize("hasAnyRole('PLANT_MANAGER', 'TECHNICIAN')")
    @Operation(summary = "Change the operating status of an asset")
    public ResponseEntity<AssetResource> changeStatus(@PathVariable Long assetId,
                                                      @Valid @RequestBody ChangeAssetStatusResource resource) {
        var asset = assetCommandService.handle(AssetResourceAssembler.toStatusCommand(assetId, resource.status()));
        return ResponseEntity.ok(AssetResourceAssembler.toResource(asset));
    }

    @PostMapping("/manuals")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Attach a manual link to an asset")
    public ResponseEntity<AssetResource> addManual(@PathVariable Long assetId,
                                                   @Valid @RequestBody AddManualResource resource) {
        var asset = assetCommandService.handle(new AddAssetManualCommand(assetId, resource.title(), resource.url()));
        return ResponseEntity.ok(AssetResourceAssembler.toResource(asset));
    }

    @DeleteMapping("/manuals/{manualIndex}")
    @PreAuthorize("hasRole('PLANT_MANAGER')")
    @Operation(summary = "Remove a manual from an asset by its position")
    public ResponseEntity<AssetResource> removeManual(@PathVariable Long assetId, @PathVariable int manualIndex) {
        var asset = assetCommandService.handle(new RemoveAssetManualCommand(assetId, manualIndex));
        return ResponseEntity.ok(AssetResourceAssembler.toResource(asset));
    }
}
