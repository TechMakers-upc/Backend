package com.techmakers.fixcore.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ReceiveStockResource(@Min(1) int quantity, @Size(max = 200) String reason) {
}
