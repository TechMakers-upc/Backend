package com.techmakers.fixcore.iam.infrastructure.authorization.sfs.services;

import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.shared.domain.exceptions.PlantAccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityContextPlantAccessGuard implements PlantAccessGuard {

    private static final String OPERATIONS_MANAGER = "ROLE_" + Roles.OPERATIONS_MANAGER.name();

    @Override
    public void ensureAccessTo(Long plantId) {
        restrictedPlantId().filter(ownPlant -> !ownPlant.equals(plantId)).ifPresent(ownPlant -> {
            throw new PlantAccessDeniedException(plantId);
        });
    }

    @Override
    public Optional<Long> restrictedPlantId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl user)) {
            return Optional.empty();
        }
        var global = user.getAuthorities().stream()
                .anyMatch(authority -> OPERATIONS_MANAGER.equals(authority.getAuthority()));
        return global ? Optional.empty() : Optional.of(user.getPlantId() == null ? -1L : user.getPlantId());
    }
}
