package com.techmakers.fixcore.assetmanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record ChangeAssetStatusResource(@NotBlank String status) {
}
