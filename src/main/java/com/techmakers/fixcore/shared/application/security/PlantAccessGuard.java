package com.techmakers.fixcore.shared.application.security;

import java.util.Optional;

public interface PlantAccessGuard {

    void ensureAccessTo(Long plantId);

    Optional<Long> restrictedPlantId();
}
