package com.techmakers.fixcore.inventory.application.outboundservices.acl;

import java.util.Optional;

public interface PlantCatalog {

    boolean existsPlant(Long plantId);

    Optional<Long> plantIdByName(String plantName);
}
