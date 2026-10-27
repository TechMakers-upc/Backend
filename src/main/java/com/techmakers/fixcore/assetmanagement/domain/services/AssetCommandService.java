package com.techmakers.fixcore.assetmanagement.domain.services;

import com.techmakers.fixcore.assetmanagement.domain.model.aggregates.Asset;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.AddAssetManualCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.ChangeAssetStatusCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.CreateAssetCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.DeleteAssetCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.RemoveAssetManualCommand;
import com.techmakers.fixcore.assetmanagement.domain.model.commands.UpdateAssetDetailsCommand;

public interface AssetCommandService {

    Asset handle(CreateAssetCommand command);

    Asset handle(UpdateAssetDetailsCommand command);

    void handle(DeleteAssetCommand command);

    Asset handle(ChangeAssetStatusCommand command);

    Asset handle(AddAssetManualCommand command);

    Asset handle(RemoveAssetManualCommand command);
}
