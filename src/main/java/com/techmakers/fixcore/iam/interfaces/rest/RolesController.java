package com.techmakers.fixcore.iam.interfaces.rest;

import com.techmakers.fixcore.iam.domain.model.queries.GetAllRolesQuery;
import com.techmakers.fixcore.iam.domain.services.UserQueryService;
import com.techmakers.fixcore.iam.interfaces.rest.resources.RoleResource;
import com.techmakers.fixcore.iam.interfaces.rest.transform.RoleAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Roles", description = "Available user roles")
public class RolesController {

    private final UserQueryService userQueryService;

    public RolesController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping
    @Operation(summary = "List the available roles")
    public ResponseEntity<List<RoleResource>> getRoles() {
        var roles = userQueryService.handle(new GetAllRolesQuery()).stream()
                .map(RoleAssembler::toResource).toList();
        return ResponseEntity.ok(roles);
    }
}
