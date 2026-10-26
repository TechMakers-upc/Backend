package com.techmakers.fixcore.serviceplanning.domain.services;

import com.techmakers.fixcore.serviceplanning.domain.model.aggregates.MaintenancePlan;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetAllMaintenancePlansQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlanByIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByAssetIdQuery;
import com.techmakers.fixcore.serviceplanning.domain.model.queries.GetMaintenancePlansByPlantIdQuery;

import java.util.List;
import java.util.Optional;

public interface MaintenancePlanQueryService {

    List<MaintenancePlan> handle(GetAllMaintenancePlansQuery query);

    List<MaintenancePlan> handle(GetMaintenancePlansByPlantIdQuery query);

    List<MaintenancePlan> handle(GetMaintenancePlansByAssetIdQuery query);

    Optional<MaintenancePlan> handle(GetMaintenancePlanByIdQuery query);
}
