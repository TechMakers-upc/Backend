package com.techmakers.fixcore.iam.interfaces.rest;

import com.techmakers.fixcore.iam.domain.model.queries.GetAllUsersQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetUserByEmailQuery;
import com.techmakers.fixcore.iam.domain.model.queries.GetUserByIdQuery;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.services.UserQueryService;
import com.techmakers.fixcore.iam.interfaces.rest.resources.UserResource;
import com.techmakers.fixcore.iam.interfaces.rest.transform.UserResourceAssembler;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User account endpoints")
public class UsersController {

    private final UserQueryService userQueryService;

    public UsersController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping
    @Operation(summary = "List users, optionally filtered by email")
    public ResponseEntity<List<UserResource>> getUsers(@RequestParam(required = false) String email) {
        if (email != null) {
            var users = userQueryService.handle(new GetUserByEmailQuery(new EmailAddress(email)))
                    .map(UserResourceAssembler::toResource).stream().toList();
            return ResponseEntity.ok(users);
        }
        var users = userQueryService.handle(new GetAllUsersQuery()).stream()
                .map(UserResourceAssembler::toResource).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get a user by identifier")
    public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
        return userQueryService.handle(new GetUserByIdQuery(userId))
                .map(user -> ResponseEntity.ok(UserResourceAssembler.toResource(user)))
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user")
    public ResponseEntity<UserResource> getCurrentUser(Principal principal) {
        return userQueryService.handle(new GetUserByEmailQuery(new EmailAddress(principal.getName())))
                .map(user -> ResponseEntity.ok(UserResourceAssembler.toResource(user)))
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getName()));
    }
}
