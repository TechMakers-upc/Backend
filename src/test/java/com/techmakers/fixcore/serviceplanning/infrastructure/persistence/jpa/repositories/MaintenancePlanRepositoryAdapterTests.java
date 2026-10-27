package com.techmakers.fixcore.serviceplanning.infrastructure.persistence.jpa.repositories;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;
import com.techmakers.fixcore.shared.infrastructure.persistence.jpa.configuration.JpaAuditingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MaintenancePlanRepositoryAdapter.class, JpaAuditingConfiguration.class})
class MaintenancePlanRepositoryAdapterTests {

    private static final long PLANT = 900L;
    private static final long ASSET = 9001L;

    @Autowired
    private MaintenancePlanRepositoryAdapter planRepository;

    @Test
    void shouldPersistCompletionAndSuspension() {
        var plan = planRepository.save(new MaintenancePlan(PLANT,
                new PlanDetails(ASSET, "Cambio de filtros", "Cambiar filtro", 30, LocalDate.of(2026, 10, 7), 3L)));

        plan.registerCompletion(LocalDate.of(2026, 10, 8));
        plan.suspend();
        planRepository.save(plan);

        var found = planRepository.findById(plan.getId()).orElseThrow();
        assertThat(found.isActive()).isFalse();
        assertThat(found.getLastCompletedAt()).isEqualTo(LocalDate.of(2026, 10, 8));
        assertThat(found.getDetails().nextDueDate()).isEqualTo(LocalDate.of(2026, 11, 7));
    }

    @Test
    void shouldListPlansByPlantAndAssetOrderedByDueDate() {
        planRepository.save(new MaintenancePlan(PLANT, new PlanDetails(ASSET, "Trimestral", "", 90, LocalDate.of(2026, 12, 1), null)));
        planRepository.save(new MaintenancePlan(PLANT, new PlanDetails(ASSET, "Mensual", "", 30, LocalDate.of(2026, 11, 1), null)));

        assertThat(planRepository.findAllByAssetId(ASSET)).extracting(p -> p.getDetails().title())
                .containsExactly("Mensual", "Trimestral");
        assertThat(planRepository.findAllByPlantId(PLANT)).hasSize(2);
        assertThat(planRepository.existsByAssetIdAndTitle(ASSET, "mensual")).isTrue();
    }
}
