package com.techmakers.fixcore.iam.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.FullName;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.configuration.JpaAuditingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepositoryAdapter.class, JpaAuditingConfiguration.class})
class UserRepositoryAdapterTests {

    @Autowired
    private UserRepositoryAdapter userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        var user = new User(new EmailAddress("Ana.Rojas@FixCore.com"), "hashed-password",
                new FullName("Ana Rojas"), "Plant manager", 1L, Roles.PLANT_MANAGER);

        var saved = userRepository.save(user);
        var found = userRepository.findByEmail(new EmailAddress("ana.rojas@fixcore.com"));

        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();
        assertThat(found.get().getFullName().value()).isEqualTo("Ana Rojas");
        assertThat(found.get().getRole()).isEqualTo(Roles.PLANT_MANAGER);
        assertThat(userRepository.existsByEmail(new EmailAddress("ana.rojas@fixcore.com"))).isTrue();
    }

    @Test
    void shouldStoreOperationsManagerWithoutPlant() {
        var user = new User(new EmailAddress("ops@fixcore.com"), "hashed-password",
                new FullName("Luis Paredes"), "Operations manager", 5L, Roles.OPERATIONS_MANAGER);

        var saved = userRepository.save(user);

        assertThat(userRepository.findById(saved.getId())).get()
                .extracting(User::getPlantId).isNull();
    }
}
