package com.techmakers.fixcore.serviceplanning.domain.model.aggregates;

import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;
import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanStanding;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaintenancePlanTests {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 26);

    @Test
    void shouldClassifyStandingByDueDate() {
        assertThat(planDueOn(TODAY.minusDays(1)).standing(TODAY)).isEqualTo(PlanStanding.OVERDUE);
        assertThat(planDueOn(TODAY.plusDays(7)).standing(TODAY)).isEqualTo(PlanStanding.DUE_SOON);
        assertThat(planDueOn(TODAY.plusDays(8)).standing(TODAY)).isEqualTo(PlanStanding.SCHEDULED);

        var suspended = planDueOn(TODAY.minusDays(10));
        suspended.suspend();
        assertThat(suspended.standing(TODAY)).isEqualTo(PlanStanding.SUSPENDED);
    }

    @Test
    void shouldMoveOverdueDateToTodayWhenReactivated() {
        var plan = planDueOn(TODAY.minusDays(20));
        plan.suspend();
        plan.reactivate(TODAY);

        assertThat(plan.isActive()).isTrue();
        assertThat(plan.getDetails().nextDueDate()).isEqualTo(TODAY);
    }

    @Test
    void shouldRollNextDateForwardWhenCompleted() {
        var plan = planDueOn(TODAY);
        plan.registerCompletion(TODAY);

        assertThat(plan.getLastCompletedAt()).isEqualTo(TODAY);
        assertThat(plan.getDetails().nextDueDate()).isEqualTo(TODAY.plusDays(30));
    }

    @Test
    void shouldRejectFrequencyOutOfRange() {
        assertThatThrownBy(() -> new PlanDetails(1L, "Revisión", "", 731, TODAY, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Frequency must be 1 to 730 days");
    }

    private static MaintenancePlan planDueOn(LocalDate nextDueDate) {
        return new MaintenancePlan(1L, new PlanDetails(10L, "Cambio de filtros", "Cambiar filtro", 30, nextDueDate, 3L));
    }
}
