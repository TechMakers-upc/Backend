package com.techmakers.fixcore.iam.infrastructure.persistence.jpa.mappers;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.FullName;
import com.techmakers.fixcore.iam.infrastructure.persistence.jpa.entities.UserEntity;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    public static UserEntity toEntity(User user) {
        var entity = new UserEntity(
                user.getEmail().address(),
                user.getPasswordHash(),
                user.getFullName().value(),
                user.getJobTitle(),
                user.getPlantId(),
                user.getRole());
        entity.setId(user.getId());
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                new EmailAddress(entity.getEmail()),
                entity.getPasswordHash(),
                new FullName(entity.getFullName()),
                entity.getJobTitle(),
                entity.getPlantId(),
                entity.getRole());
    }
}
