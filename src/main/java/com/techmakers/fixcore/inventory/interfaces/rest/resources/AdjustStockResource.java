package com.techmakers.fixcore.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdjustStockResource(@Min(0) int countedStock, @NotBlank @Size(max = 200) String reason) {
}
