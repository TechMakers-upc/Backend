package com.techmakers.fixcore.iam.application.internal.commandservices;

import com.techmakers.fixcore.iam.application.outboundservices.hashing.HashingService;
import com.techmakers.fixcore.iam.application.outboundservices.tokens.TokenService;
import com.techmakers.fixcore.iam.domain.exceptions.InvalidCredentialsException;
import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.commands.SignInCommand;
import com.techmakers.fixcore.iam.domain.model.commands.SignUpCommand;
import com.techmakers.fixcore.iam.domain.model.valueobjects.AuthenticatedUser;
import com.techmakers.fixcore.iam.domain.repositories.UserRepository;
import com.techmakers.fixcore.iam.domain.services.UserCommandService;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public User handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new ResourceAlreadyExistsException("User", "email", command.email().address());
        }
        var user = new User(command.email(), hashingService.encode(command.password()), command.fullName(),
                command.jobTitle(), command.plantId(), command.role());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedUser handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email())
                .filter(candidate -> hashingService.matches(command.password(), candidate.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return new AuthenticatedUser(user, tokenService.generateToken(user.getEmail().address()));
    }
}
