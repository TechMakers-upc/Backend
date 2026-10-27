package com.techmakers.fixcore.iam.application.outboundservices.acl;

import java.util.Optional;

public interface PlantDirectory {

    boolean exists(Long plantId);

    Optional<Long> plantIdByName(String plantName);
}
