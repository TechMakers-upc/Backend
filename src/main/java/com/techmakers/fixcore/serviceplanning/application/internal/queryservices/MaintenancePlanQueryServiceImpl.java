package com.techmakers.fixcore.serviceplanning.application.internal.queryservices;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetAllMaintenancePlansQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlanByIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByAssetIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByPlantIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.repositories.MaintenancePlanRepository;
import com.techmakers.fixcore.serviceplanning.domain.services.MaintenancePlanQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MaintenancePlanQueryServiceImpl implements MaintenancePlanQueryService {

    private final MaintenancePlanRepository planRepository;

    public MaintenancePlanQueryServiceImpl(MaintenancePlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public List<MaintenancePlan> handle(GetAllMaintenancePlansQuery query) {
        return planRepository.findAll();
    }

    @Override
    public List<MaintenancePlan> handle(GetMaintenancePlansByPlantIdQuery query) {
        return planRepository.findAllByPlantId(query.plantId());
    }

    @Override
    public List<MaintenancePlan> handle(GetMaintenancePlansByAssetIdQuery query) {
        return planRepository.findAllByAssetId(query.assetId());
    }

    @Override
    public Optional<MaintenancePlan> handle(GetMaintenancePlanByIdQuery query) {
        return planRepository.findById(query.planId());
    }
}
