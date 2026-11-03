package com.techmakers.fixcore.iam.infrastructure.authorization.sfs.services;

import com.techmakers.fixcore.iam.application.outboundservices.acl.UserProvisioningPolicy;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SecurityContextUserProvisioningPolicy implements UserProvisioningPolicy {

    @Override
    public void ensureCanCreate(Roles requestedRole, Long requestedPlantId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserDetailsImpl current)) {
            throw new AccessDeniedException("Authentication is required to create users");
        }

        boolean operationsManager = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        "ROLE_OPERATIONS_MANAGER".equals(authority.getAuthority()));

        if (operationsManager) {
            return;
        }

        boolean plantManager = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        "ROLE_PLANT_MANAGER".equals(authority.getAuthority()));

        if (!plantManager
                || requestedRole != Roles.TECHNICIAN
                || !Objects.equals(current.getPlantId(), requestedPlantId)) {
            throw new AccessDeniedException(
                    "A plant manager can only create technicians in their own plant");
        }
    }
}
