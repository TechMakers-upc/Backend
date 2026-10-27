package com.techmakers.fixcore.serviceexecution.application.outboundservices.acl;

import java.time.LocalDate;

public interface PlanCompletionRecorder {

    void registerCompletion(Long planId, LocalDate completedOn);
}
