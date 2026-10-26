package com.techmakers.fixcore.iam.interfaces.rest;

import com.techmakers.fixcore.iam.domain.services.UserCommandService;
import com.techmakers.fixcore.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.techmakers.fixcore.iam.interfaces.rest.resources.SignInResource;
import com.techmakers.fixcore.iam.interfaces.rest.resources.SignUpResource;
import com.techmakers.fixcore.iam.interfaces.rest.resources.UserResource;
import com.techmakers.fixcore.iam.interfaces.rest.transform.AuthenticationCommandAssembler;
import com.techmakers.fixcore.iam.interfaces.rest.transform.UserResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Sign up and sign in endpoints")
public class AuthenticationController {

    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<UserResource> signUp(@Valid @RequestBody SignUpResource resource) {
        var user = userCommandService.handle(AuthenticationCommandAssembler.toCommand(resource));
        var location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(UserResourceAssembler.toResource(user));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Authenticate a user and issue a bearer token")
    public ResponseEntity<AuthenticatedUserResource> signIn(@Valid @RequestBody SignInResource resource) {
        var authenticatedUser = userCommandService.handle(AuthenticationCommandAssembler.toCommand(resource));
        return ResponseEntity.ok(UserResourceAssembler.toResource(authenticatedUser));
    }
}
