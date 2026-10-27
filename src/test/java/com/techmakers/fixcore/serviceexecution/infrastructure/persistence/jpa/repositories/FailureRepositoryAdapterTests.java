package com.techmakers.fixcore.serviceexecution.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureKind;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureStatus;
import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.Priority;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.configuration.JpaAuditingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FailureRepositoryAdapter.class, JpaAuditingConfiguration.class})
class FailureRepositoryAdapterTests {

    private static final long PLANT = 600L;
    private static final long ASSET = 6001L;

    @Autowired
    private FailureRepositoryAdapter failureRepository;

    @Test
    void shouldPersistFailureLifecycleAndListNewestFirst() {
        var older = failureRepository.save(Failure.report(PLANT, ASSET, FailureKind.LEAK, "Goteo", false, 3L,
                Instant.parse("2026-09-24T10:00:00Z")));
        failureRepository.save(Failure.report(PLANT, ASSET, FailureKind.JAM, "Atasco", true, 4L,
                Instant.parse("2026-10-06T10:00:00Z")));

        older.prioritize(Priority.HIGH);
        older.attachWorkOrder(91L);
        older.resolve(Instant.parse("2026-09-24T18:00:00Z"));
        failureRepository.save(older);

        var found = failureRepository.findById(older.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(FailureStatus.RESOLVED);
        assertThat(found.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(found.getWorkOrderId()).isEqualTo(91L);
        assertThat(failureRepository.findAllByAssetId(ASSET)).extracting(Failure::getKind)
                .containsExactly(FailureKind.JAM, FailureKind.LEAK);
        assertThat(failureRepository.existsByAssetIdAndReportedAt(ASSET, Instant.parse("2026-10-06T10:00:00Z"))).isTrue();
    }
}
