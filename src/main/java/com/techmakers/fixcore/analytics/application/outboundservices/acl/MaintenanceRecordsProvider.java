package com.techmakers.fixcore.analytics.application.outboundservices.acl;

import com.techmakers.fixcore.analytics.domain.model.valueobjects.*;

import java.util.Collection;
import java.util.List;

public interface MaintenanceRecordsProvider {

    List<Long> allPlantIds();

    List<MachineRecord> machinesOf(Collection<Long> plantIds);

    List<FailureRecord> failuresOf(Collection<Long> plantIds);

    List<OrderRecord> ordersOf(Collection<Long> plantIds);

    List<PlanRecord> plansOf(Collection<Long> plantIds);

    List<PartRecord> partsOf(Collection<Long> plantIds);

    long techniciansOf(Collection<Long> plantIds);
}
