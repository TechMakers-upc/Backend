package com.techmakers.fixcore.serviceexecution.domain.services;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailureByIdQuery;
import com.techmakers.fixcore.serviceexecution.domain.model.queries.GetFailuresQuery;

import java.util.List;
import java.util.Optional;

public interface FailureQueryService {

    List<Failure> handle(GetFailuresQuery query);

    Optional<Failure> handle(GetFailureByIdQuery query);
}
