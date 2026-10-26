package com.techmakers.fixcore.iam.domain.repositories;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);

    List<User> findAll();
}
