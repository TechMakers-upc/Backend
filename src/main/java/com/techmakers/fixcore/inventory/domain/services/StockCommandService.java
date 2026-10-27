package com.techmakers.fixcore.inventory.domain.services;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.commands.AdjustStockCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.ConsumeStockCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.ReceiveStockCommand;

public interface StockCommandService {

    SparePart handle(ReceiveStockCommand command);

    SparePart handle(AdjustStockCommand command);

    SparePart handle(ConsumeStockCommand command);
}
