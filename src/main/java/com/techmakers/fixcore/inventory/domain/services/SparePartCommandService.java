package com.techmakers.fixcore.inventory.domain.services;

import com.techmakers.fixcore.inventory.domain.model.aggregates.SparePart;
import com.techmakers.fixcore.inventory.domain.model.commands.CreateSparePartCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.DeleteSparePartCommand;
import com.techmakers.fixcore.inventory.domain.model.commands.UpdateSparePartDetailsCommand;

public interface SparePartCommandService {

    SparePart handle(CreateSparePartCommand command);

    SparePart handle(UpdateSparePartDetailsCommand command);

    void handle(DeleteSparePartCommand command);
}
