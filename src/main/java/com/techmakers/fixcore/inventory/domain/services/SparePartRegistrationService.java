package com.techmakers.fixcore.inventory.domain.services;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.commands.CreateSparePartCommand;

public interface SparePartRegistrationService {

    SparePart register(CreateSparePartCommand command,
                       Integer initialStock,
                       String createdBy);
}
