package com.techmakers.fixcore.serviceexecution.application.outboundservices.acl;

import java.util.Optional;

public interface PlanCatalog {

    Optional<Long> assetIdOfPlan(Long planId);

    Optional<Long> planIdByAssetAndTitle(Long assetId, String title);
}
