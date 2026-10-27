package com.techmakers.fixcore.serviceexecution.application.internal.queryservices;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.WorkOrder;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetWorkOrderByIdQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetWorkOrdersQuery;
import com.techmakers.fixcore.serviceexecution.domain.repositories.WorkOrderRepository;
import com.techmakers.fixcore.serviceexecution.domain.services.WorkOrderQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class WorkOrderQueryServiceImpl implements WorkOrderQueryService {

    private static final Comparator<WorkOrder> BOARD_ORDER = Comparator
            .comparing((WorkOrder order) -> !order.isOpen())
            .thenComparing(order -> order.getDetails().priority())
            .thenComparing(order -> order.getDetails().dueDate());

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderQueryServiceImpl(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    public List<WorkOrder> handle(GetWorkOrdersQuery query) {
        var orders = query.plantId() == null ? workOrderRepository.findAll()
                : workOrderRepository.findAllByPlantId(query.plantId());
        return orders.stream()
                .filter(order -> query.assetId() == null || query.assetId().equals(order.getAssetId()))
                .filter(order -> query.technicianId() == null || query.technicianId().equals(order.getTechnicianId()))
                .filter(order -> query.status() == null || query.status() == order.getStatus())
                .sorted(BOARD_ORDER)
                .toList();
    }

    @Override
    public Optional<WorkOrder> handle(GetWorkOrderByIdQuery query) {
        return workOrderRepository.findById(query.workOrderId());
    }
}
