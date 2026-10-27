package com.techmakers.fixcore.iam.infrastructure.authorization.sfs.services;

import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.shared.domain.exceptions.PlantAccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextPlantAccessGuard implements PlantAccessGuard {

    private static final String OPERATIONS_MANAGER = "ROLE_" + Roles.OPERATIONS_MANAGER.name();

    @Override
    public void ensureAccessTo(Long plantId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl user)) {
            return;
        }
        var global = user.getAuthorities().stream()
                .anyMatch(authority -> OPERATIONS_MANAGER.equals(authority.getAuthority()));
        if (!global && !plantId.equals(user.getPlantId())) {
            throw new PlantAccessDeniedException(plantId);
        }
    }
}
