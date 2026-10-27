package com.techmakers.fixcore.shared.application.security;

public interface PlantAccessGuard {

    void ensureAccessTo(Long plantId);
}
