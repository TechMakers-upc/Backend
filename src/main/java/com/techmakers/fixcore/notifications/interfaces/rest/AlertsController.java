package com.techmakers.fixcore.notifications.interfaces.rest;

import com.techmakers.fixcore.notifications.application.outboundservices.acl.AlertReadStore;
import com.techmakers.fixcore.notifications.domain.model.queries.GetAlertsQuery;
import com.techmakers.fixcore.notifications.domain.services.NotificationQueryService;
import com.techmakers.fixcore.notifications.interfaces.rest.resources.AlertResource;
import com.techmakers.fixcore.notifications.interfaces.rest.transform.AlertResourceAssembler;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Alerts", description = "Role-based alert endpoints")
public class AlertsController {

    private final NotificationQueryService notificationQueryService;
    private final AlertReadStore alertReadStore;

    public AlertsController(NotificationQueryService notificationQueryService,
                            AlertReadStore alertReadStore) {
        this.notificationQueryService = notificationQueryService;
        this.alertReadStore = alertReadStore;
    }

    @GetMapping
    @Operation(summary = "Get the alerts for the signed-in user, most severe first")
    public ResponseEntity<List<AlertResource>> getAlerts(Principal principal) {
        var alerts = notificationQueryService.handle(new GetAlertsQuery(principal.getName()));
        var readIds = alertReadStore.readIds(principal.getName());

        return ResponseEntity.ok(alerts.stream()
                .map(alert -> AlertResourceAssembler.toResource(
                        alert, readIds.contains(alert.id())))
                .toList());
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Count unread alerts for the signed-in user")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Principal principal) {
        var email = principal.getName();
        var readIds = alertReadStore.readIds(email);

        long unread = notificationQueryService
                .handle(new GetAlertsQuery(email))
                .stream()
                .filter(alert -> !readIds.contains(alert.id()))
                .count();

        return ResponseEntity.ok(Map.of("unread", unread));
    }

    @PatchMapping("/{alertId}/read")
    @Operation(summary = "Mark a visible alert as read")
    public ResponseEntity<Void> markRead(@PathVariable String alertId,
                                         Principal principal) {
        var email = principal.getName();

        boolean visible = notificationQueryService
                .handle(new GetAlertsQuery(email))
                .stream()
                .anyMatch(alert -> alert.id().equals(alertId));

        if (!visible) {
            throw new ResourceNotFoundException("Alert", alertId);
        }

        alertReadStore.markRead(email, alertId);
        return ResponseEntity.noContent().build();
    }
}
