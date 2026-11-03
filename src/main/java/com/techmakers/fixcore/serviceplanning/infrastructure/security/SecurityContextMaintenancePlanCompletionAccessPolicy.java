package com.techmakers.fixcore.serviceplanning.infrastructure.security;

import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.techmakers.fixcore.serviceplanning.application.outboundservices.acl.MaintenancePlanCompletionAccessPolicy;
import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.shared.domain.exceptions.MaintenancePlanAccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SecurityContextMaintenancePlanCompletionAccessPolicy
        implements MaintenancePlanCompletionAccessPolicy {

    @Override
    public void ensureCanComplete(MaintenancePlan plan) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserDetailsImpl user)) {
            throw new MaintenancePlanAccessDeniedException();
        }

        if (!Objects.equals(user.getPlantId(), plan.getPlantId())) {
            throw new MaintenancePlanAccessDeniedException();
        }

        if (hasRole(user, Roles.PLANT_MANAGER)) {
            return;
        }

        if (hasRole(user, Roles.TECHNICIAN)
                && Objects.equals(user.getId(), plan.getDetails().technicianId())) {
            return;
        }

        throw new MaintenancePlanAccessDeniedException();
    }

    private boolean hasRole(UserDetailsImpl user, Roles role) {
        var expected = "ROLE_" + role.name();

        return user.getAuthorities().stream()
                .anyMatch(authority -> expected.equals(authority.getAuthority()));
    }
}
