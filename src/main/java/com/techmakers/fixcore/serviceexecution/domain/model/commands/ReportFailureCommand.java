package com.techmakers.fixcore.serviceexecution.domain.model.commands;

import com.techmakers.fixcore.serviceexecution.domain.model.valueobjects.FailureKind;

public record ReportFailureCommand(Long assetId, FailureKind kind, String description, boolean lineStopped,
                                   String reportedBy) {

    public ReportFailureCommand {
        if (assetId == null || assetId <= 0) throw new IllegalArgumentException("Asset identifier must be a positive number");
        if (kind == null) throw new IllegalArgumentException("Failure kind is required");
        if (reportedBy == null || reportedBy.isBlank()) throw new IllegalArgumentException("The reporter is required");
    }
}
