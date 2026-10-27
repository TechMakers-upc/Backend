package com.techmakers.fixcore.serviceplanning.interfaces.acl;

import java.time.LocalDate;

public record PlanSummary(Long id, Long plantId, Long assetId, String title, LocalDate nextDueDate, String standing) {
}
