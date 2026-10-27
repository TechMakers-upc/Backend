package com.techmakers.fixcore.serviceexecution.application.outboundservices.acl;

import java.util.Optional;

public interface PartCatalog {

    Optional<Long> plantIdOfPart(Long partId);

    Optional<Long> partIdByPlantAndSku(Long plantId, String sku);
}
