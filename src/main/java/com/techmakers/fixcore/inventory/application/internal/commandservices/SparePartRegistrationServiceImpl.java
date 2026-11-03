package com.techmakers.fixcore.inventory.application.internal.commandservices;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.commands.CreateSparePartCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.ReceiveStockCommand;
import com.techmakers.fixcore.inventory.domain.services.SparePartCommandService;
import com.techmakers.fixcore.inventory.domain.services.SparePartRegistrationService;
import com.techmakers.fixcore.inventory.domain.services.StockCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SparePartRegistrationServiceImpl implements SparePartRegistrationService {

    private static final String INITIAL_STOCK_REASON = "initial-stock";

    private final SparePartCommandService partCommandService;
    private final StockCommandService stockCommandService;

    public SparePartRegistrationServiceImpl(
            SparePartCommandService partCommandService,
            StockCommandService stockCommandService) {
        this.partCommandService = partCommandService;
        this.stockCommandService = stockCommandService;
    }

    @Override
    @Transactional
    public SparePart register(CreateSparePartCommand command,
                              Integer initialStock,
                              String createdBy) {
        var part = partCommandService.handle(command);

        if (initialStock != null && initialStock > 0) {
            return stockCommandService.handle(
                    new ReceiveStockCommand(
                            part.getId(),
                            initialStock,
                            INITIAL_STOCK_REASON,
                            createdBy
                    )
            );
        }

        return part;
    }
}
