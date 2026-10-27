package com.techmakers.fixcore.serviceexecution.application.outboundservices.acl;

import java.util.Optional;

public interface AssetCatalog {

    Optional<Long> plantIdOfAsset(Long assetId);

    Optional<Long> assetIdByPlantNameAndCode(String plantName, String assetCode);
}
