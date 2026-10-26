package com.techmakers.fixcore.iam.domain.services;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.queries.GetAllRolesQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetAllUsersQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetUserByEmailQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetUserByIdQuery;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {

    List<User> handle(GetAllUsersQuery query);

    Optional<User> handle(GetUserByIdQuery query);

    Optional<User> handle(GetUserByEmailQuery query);

    List<Roles> handle(GetAllRolesQuery query);
}
