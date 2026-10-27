package com.techmakers.fixcore.iam.interfaces.acl;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.queries.GetUserByEmailQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetUserByIdQuery;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.domain.services.UserQueryService;
import com.techmakers.fixcore.iam.domain.model.queries.GetAllUsersQuery;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class IamContextFacade {

    private final UserQueryService userQueryService;

    public IamContextFacade(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public Optional<Long> fetchUserIdByEmail(String email) {
        return userQueryService.handle(new GetUserByEmailQuery(new EmailAddress(email))).map(User::getId);
    }

    public boolean isTechnician(Long userId) {
        return userQueryService.handle(new GetUserByIdQuery(userId))
                .map(user -> user.getRole() == Roles.TECHNICIAN)
                .orElse(false);
    }

    public Optional<AccountSummary> fetchAccountByEmail(String email) {
        return userQueryService.handle(new GetUserByEmailQuery(new EmailAddress(email)))
                .map(user -> new AccountSummary(user.getId(), user.getRole().name(), user.getPlantId()));
    }

    public long countTechniciansInPlants(Collection<Long> plantIds) {
        return userQueryService.handle(new GetAllUsersQuery()).stream()
                .filter(user -> user.getRole() == Roles.TECHNICIAN && plantIds.contains(user.getPlantId()))
                .count();
    }
}
