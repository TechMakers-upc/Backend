package com.techmakers.fixcore.iam.application.internal.queryservices;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.queries.GetAllRolesQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetAllUsersQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetUserByEmailQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetUserByIdQuery;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.domain.repositories.UserRepository;
import com.techmakers.fixcore.iam.domain.services.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }

    @Override
    public List<Roles> handle(GetAllRolesQuery query) {
        return Arrays.asList(Roles.values());
    }
}
