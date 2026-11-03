package com.techmakers.fixcore.iam.application.internal.eventhandlers;

import com.techmakers.fixcore.iam.application.outboundservices.hashing.HashingService;
import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.FullName;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(
        name = "fixcore.bootstrap.enabled",
        havingValue = "true"
)
public class BootstrapOperationsManager implements ApplicationRunner {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final String email;
    private final String password;

    public BootstrapOperationsManager(
            UserRepository userRepository,
            HashingService hashingService,
            @Value("${fixcore.bootstrap.email}") String email,
            @Value("${fixcore.bootstrap.password}") String password) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        if (!userRepository.findAll().isEmpty()) {
            return;
        }

        if (password == null || password.length() < 12) {
            throw new IllegalStateException(
                    "Bootstrap password must contain at least 12 characters");
        }

        var administrator = new User(
                new EmailAddress(email),
                hashingService.encode(password),
                new FullName("Operations Administrator"),
                "Operations Manager",
                null,
                Roles.OPERATIONS_MANAGER
        );

        userRepository.save(administrator);
    }
}
