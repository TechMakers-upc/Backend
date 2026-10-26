package com.techmakers.fixcore.iam.domain.services;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.commands.SignInCommand;
import com.techmakers.fixcore.iam.domain.model.commands.SignUpCommand;
import com.techmakers.fixcore.iam.domain.model.valueobjects.AuthenticatedUser;

public interface UserCommandService {

    User handle(SignUpCommand command);

    AuthenticatedUser handle(SignInCommand command);
}
