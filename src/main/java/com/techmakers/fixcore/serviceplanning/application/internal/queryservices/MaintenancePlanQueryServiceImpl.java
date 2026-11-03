package com.techmakers.fixcore.serviceplanning.application.internal.queryservices;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetAllMaintenancePlansQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlanByIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByAssetIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByPlantIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.repositories.MaintenancePlanRepository;
import com.techmakers.fixcore.shared.application.security.PlantAccessGuard;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MaintenancePlanQueryServiceImpl implements MaintenancePlanQueryService {

    private final MaintenancePlanRepository planRepository;
    private final PlantAccessGuard plantAccessGuard;

    public MaintenancePlanQueryServiceImpl(MaintenancePlanRepository planRepository,
                                            PlantAccessGuard plantAccessGuard) {
        this.planRepository = planRepository;
        this.plantAccessGuard = plantAccessGuard;
    }

    @Override
    public List<MaintenancePlan> handle(GetAllMaintenancePlansQuery query) {
        return planRepository.findAll().stream().filter(plan -> canRead(plan.getPlantId())).toList();
    }

    @Override
    public List<MaintenancePlan> handle(GetMaintenancePlansByPlantIdQuery query) {
        plantAccessGuard.ensureAccessTo(query.plantId());
        return planRepository.findAllByPlantId(query.plantId());
    }

    @Override
    public List<MaintenancePlan> handle(GetMaintenancePlansByAssetIdQuery query) {
        return planRepository.findAllByAssetId(query.assetId()).stream().filter(plan -> canRead(plan.getPlantId())).toList();
    }

    @Override
    public Optional<MaintenancePlan> handle(GetMaintenancePlanByIdQuery query) {
        return planRepository.findById(query.planId())
                .map(plan -> {
                    plantAccessGuard.ensureAccessTo(plan.getPlantId());
                    return plan;
                });
    }

    private boolean canRead(Long plantId) {
        return plantAccessGuard.restrictedPlantId()
                .map(restricted -> restricted.equals(plantId))
                .orElse(true);
    }
}
