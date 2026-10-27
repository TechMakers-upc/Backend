package com.techmakers.fixcore.serviceplanning.application.internal.commandservices;

import com.techmakers.fixcore.serviceplanning.application.outboundservices.acl.AssetCatalog;
import com.techmakers.fixcore.serviceplanning.application.outboundservices.acl.TechnicianDirectory;
import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.commands.*;
import com.techmakers.fixcore.serviceplanning.domain.model.valueobjects.PlanDetails;
import com.techmakers.fixcore.serviceplanning.domain.repositories.MaintenancePlanRepository;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanCommandService;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class MaintenancePlanCommandServiceImpl implements MaintenancePlanCommandService {

    private final MaintenancePlanRepository planRepository;
    private final AssetCatalog assetCatalog;
    private final TechnicianDirectory technicianDirectory;
    private final PlantAccessGuard plantAccessGuard;

    public MaintenancePlanCommandServiceImpl(MaintenancePlanRepository planRepository, AssetCatalog assetCatalog,
                                             TechnicianDirectory technicianDirectory, PlantAccessGuard plantAccessGuard) {
        this.planRepository = planRepository;
        this.assetCatalog = assetCatalog;
        this.technicianDirectory = technicianDirectory;
        this.plantAccessGuard = plantAccessGuard;
    }

    @Override
    public MaintenancePlan handle(CreateMaintenancePlanCommand command) {
        var plantId = plantOf(command.details());
        plantAccessGuard.ensureAccessTo(plantId);
        ensureTechnician(command.details());
        return planRepository.save(new MaintenancePlan(plantId, command.details()));
    }

    @Override
    public MaintenancePlan handle(UpdateMaintenancePlanCommand command) {
        var plan = findPlan(command.planId());
        if (!plantOf(command.details()).equals(plan.getPlantId())) {
            throw new BusinessRuleViolationException("A plan can only target assets of its own plant");
        }
        ensureTechnician(command.details());
        plan.updateDetails(command.details());
        return planRepository.save(plan);
    }

    @Override
    public void handle(DeleteMaintenancePlanCommand command) {
        findPlan(command.planId());
        planRepository.deleteById(command.planId());
    }

    @Override
    public MaintenancePlan handle(RescheduleMaintenancePlanCommand command) {
        var plan = findPlan(command.planId());
        plan.reschedule(command.nextDueDate());
        return planRepository.save(plan);
    }

    @Override
    public MaintenancePlan handle(SuspendMaintenancePlanCommand command) {
        var plan = findPlan(command.planId());
        plan.suspend();
        return planRepository.save(plan);
    }

    @Override
    public MaintenancePlan handle(ReactivateMaintenancePlanCommand command) {
        var plan = findPlan(command.planId());
        plan.reactivate(LocalDate.now());
        return planRepository.save(plan);
    }

    @Override
    public MaintenancePlan handle(RegisterPlanCompletionCommand command) {
        var plan = findPlan(command.planId());
        plan.registerCompletion(command.completedOn());
        return planRepository.save(plan);
    }

    private Long plantOf(PlanDetails details) {
        return assetCatalog.plantIdOfAsset(details.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", details.assetId()));
    }

    private void ensureTechnician(PlanDetails details) {
        if (details.technicianId() != null && !technicianDirectory.isTechnician(details.technicianId())) {
            throw new BusinessRuleViolationException("User %d is not a technician".formatted(details.technicianId()));
        }
    }

    private MaintenancePlan findPlan(Long planId) {
        var plan = planRepository.findById(planId).orElseThrow(() -> new ResourceNotFoundException("MaintenancePlan", planId));
        plantAccessGuard.ensureAccessTo(plan.getPlantId());
        return plan;
    }
}
