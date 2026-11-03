package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WorkOrderCodeLockJpaRepositoryTests {

    @Autowired
    private WorkOrderCodeLockJpaRepository repository;

    @Test
    void shouldFindAndLockSingletonAllocationRow() {
        var lock = repository.acquireCodeLock();

        assertThat(lock).isPresent();
        assertThat(lock.orElseThrow().getId()).isEqualTo(1L);
    }
}
