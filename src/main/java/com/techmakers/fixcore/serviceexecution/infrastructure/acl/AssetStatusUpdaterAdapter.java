package com.techmakers.fixcore.serviceexecution.infrastructure.acl;

import com.techmakers.fixcore.assetmanagement.interfaces.acl.AssetsContextFacade;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.AssetStatusUpdater;
import org.springframework.stereotype.Component;

@Component
public class AssetStatusUpdaterAdapter implements AssetStatusUpdater {

    private final AssetsContextFacade assetsContextFacade;

    public AssetStatusUpdaterAdapter(AssetsContextFacade assetsContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
    }

    @Override
    public void markDown(Long assetId) {
        assetsContextFacade.markAssetDown(assetId);
    }

    @Override
    public void markInMaintenanceIfOperational(Long assetId) {
        assetsContextFacade.markAssetInMaintenanceIfOperational(assetId);
    }

    @Override
    public void markOperational(Long assetId) {
        assetsContextFacade.markAssetOperational(assetId);
    }
}
