package com.techmakers.fixcore.serviceexecution.domain.model.queries;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureStatus;

public record GetFailuresQuery(Long plantId, Long assetId, FailureStatus status) {
}
