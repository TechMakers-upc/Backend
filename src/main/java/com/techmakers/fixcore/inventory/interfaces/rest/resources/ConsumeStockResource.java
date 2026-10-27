package com.techmakers.fixcore.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ConsumeStockResource(@Min(1) int quantity, Long workOrderId, @Size(max = 200) String reason) {
}
