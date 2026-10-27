package com.techmakers.fixcore.serviceexecution.application.outboundservices.acl;

public interface AssetStatusUpdater {

    void markDown(Long assetId);

    void markInMaintenanceIfOperational(Long assetId);

    void markOperational(Long assetId);
}
