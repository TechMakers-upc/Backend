package com.techmakers.fixcore.serviceexecution.domain.services;

import com.techmakers.fixcore.serviceexecution.domain.model.aggregates.Failure;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.PrioritizeFailureCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.ReportFailureCommand;
import com.techmakers.fixcore.serviceexecution.domain.model.commands.ResolveFailureCommand;

public interface FailureCommandService {

    Failure handle(ReportFailureCommand command);

    Failure handle(PrioritizeFailureCommand command);

    Failure handle(ResolveFailureCommand command);
}
