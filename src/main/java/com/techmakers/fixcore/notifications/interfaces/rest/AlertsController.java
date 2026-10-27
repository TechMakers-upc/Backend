package com.techmakers.fixcore.notifications.interfaces.rest;

import com.techmakers.fixcore.notifications.domain.model.queries.GetAlertsQuery;
import com.techmakers.fixcore.notifications.domain.services.NotificationQueryService;
import com.techmakers.fixcore.notifications.interfaces.rest.resources.AlertResource;
import com.techmakers.fixcore.notifications.interfaces.rest.transform.AlertResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Alerts", description = "Role-based alert endpoints")
public class AlertsController {

    private final NotificationQueryService notificationQueryService;

    public AlertsController(NotificationQueryService notificationQueryService) {
        this.notificationQueryService = notificationQueryService;
    }

    @GetMapping
    @Operation(summary = "Get the alerts for the signed-in user, most severe first")
    public ResponseEntity<List<AlertResource>> getAlerts(Principal principal) {
        var alerts = notificationQueryService.handle(new GetAlertsQuery(principal.getName()));
        return ResponseEntity.ok(alerts.stream().map(AlertResourceAssembler::toResource).toList());
    }
}
