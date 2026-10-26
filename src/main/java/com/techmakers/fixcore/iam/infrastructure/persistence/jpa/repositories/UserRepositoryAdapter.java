package com.techmakers.fixcore.iam.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.repositories.UserRepository;
import com.techmakers.fixcore.iam.infrastructure.persistence.jpa.mappers.UserPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        var saved = userJpaRepository.save(UserPersistenceMapper.toEntity(user));
        return UserPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return userJpaRepository.findByEmail(email.address()).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return userJpaRepository.existsByEmail(email.address());
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream().map(UserPersistenceMapper::toDomain).toList();
    }
}
