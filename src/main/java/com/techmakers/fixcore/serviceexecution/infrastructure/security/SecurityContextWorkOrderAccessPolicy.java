package com.techmakers.fixcore.serviceexecution.infrastructure.security;

import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.techmakers.fixcore.serviceexecution.application.outboundservices.acl.WorkOrderAccessPolicy;
import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.shared.domain.exceptions.WorkOrderAccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SecurityContextWorkOrderAccessPolicy implements WorkOrderAccessPolicy {

    @Override
    public boolean canRead(WorkOrder order) {
        var user = currentUser();

        if (user == null || order == null) {
            return false;
        }

        if (hasRole(user, Roles.OPERATIONS_MANAGER)) {
            return true;
        }

        if (!Objects.equals(user.getPlantId(), order.getPlantId())) {
            return false;
        }

        if (hasRole(user, Roles.PLANT_MANAGER)) {
            return true;
        }

        return hasRole(user, Roles.TECHNICIAN)
                && Objects.equals(user.getId(), order.getTechnicianId());
    }

    @Override
    public void ensureCanRead(WorkOrder order) {
        if (!canRead(order)) {
            throw new WorkOrderAccessDeniedException();
        }
    }

    @Override
    public void ensureCanExecute(WorkOrder order) {
        var user = currentUser();

        if (user == null || order == null) {
            throw new WorkOrderAccessDeniedException();
        }

        if (!Objects.equals(user.getPlantId(), order.getPlantId())) {
            throw new WorkOrderAccessDeniedException();
        }

        if (hasRole(user, Roles.PLANT_MANAGER)) {
            return;
        }

        if (hasRole(user, Roles.TECHNICIAN)
                && Objects.equals(user.getId(), order.getTechnicianId())) {
            return;
        }

        throw new WorkOrderAccessDeniedException();
    }

    private UserDetailsImpl currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (!(authentication.getPrincipal() instanceof UserDetailsImpl user)) {
            return null;
        }

        return user;
    }

    private boolean hasRole(UserDetailsImpl user, Roles role) {
        var expected = "ROLE_" + role.name();

        return user.getAuthorities().stream()
                .anyMatch(authority -> expected.equals(authority.getAuthority()));
    }
}
