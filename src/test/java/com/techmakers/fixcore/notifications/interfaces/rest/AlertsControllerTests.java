package com.techmakers.fixcore.notifications.interfaces.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-assets.enabled=true",
        "fixcore.seed.demo-users.enabled=true", "fixcore.seed.demo-maintenance-plans.enabled=true",
        "fixcore.seed.demo-spare-parts.enabled=true", "fixcore.seed.demo-failures.enabled=true",
        "fixcore.seed.demo-work-orders.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlertsControllerTests {

    private static final String ALERTS = "/api/v1/alerts";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGiveTechniciansTheirAssignedOrders() throws Exception {
        var json = alertsFor("jose.ramirez@fixcore.demo");
        List<String> kinds = JsonPath.read(json, "$[*].kind");
        List<String> links = JsonPath.read(json, "$[?(@.kind == 'work-order-assigned')].link");

        assertThat(kinds).contains("work-order-assigned").allMatch(kind -> kind.equals("work-order-assigned")
                || kind.equals("critical-failure"));
        assertThat(links).allMatch(link -> link.startsWith("/work-orders/"));
    }

    @Test
    void shouldGiveManagersPlanAndStockAlertsSortedBySeverity() throws Exception {
        var json = alertsFor("carla.garcia@fixcore.demo");
        List<String> kinds = JsonPath.read(json, "$[*].kind");
        List<String> severities = JsonPath.read(json, "$[*].severity");
        var rank = List.of("down", "warn", "info");

        assertThat(kinds).doesNotContain("work-order-assigned").contains("low-stock");
        assertThat(severities).isSortedAccordingTo((a, b) -> rank.indexOf(a) - rank.indexOf(b));
    }

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get(ALERTS)).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldPersistReadStatusAndUpdateUnreadCount() throws Exception {
        var email = "jose.ramirez@fixcore.demo";
        var token = tokenFor(email);

        List<String> ids = JsonPath.read(
                alertsFor(email),
                "$[?(@.kind == 'work-order-assigned')].id"
        );

        assertThat(ids).isNotEmpty();
        var alertId = ids.get(0);

        var beforeResponse = mockMvc.perform(
                        get(ALERTS + "/unread-count")
                                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int before = ((Number) JsonPath.read(
                beforeResponse, "$.unread")).intValue();

        mockMvc.perform(
                        patch(ALERTS + "/{alertId}/read", alertId)
                                .header("Authorization", token))
                .andExpect(status().isNoContent());

        var updated = alertsFor(email);

        List<Boolean> flags = JsonPath.read(
                updated,
                "$[?(@.id == '" + alertId + "')].read"
        );

        assertThat(flags).containsExactly(true);

        var afterResponse = mockMvc.perform(
                        get(ALERTS + "/unread-count")
                                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int after = ((Number) JsonPath.read(
                afterResponse, "$.unread")).intValue();

        assertThat(after).isEqualTo(before - 1);

        mockMvc.perform(
                        patch(ALERTS + "/{alertId}/read", alertId)
                                .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowUserToMarkAnotherUsersAlert() throws Exception {
        List<String> ids = JsonPath.read(
                alertsFor("jose.ramirez@fixcore.demo"),
                "$[?(@.kind == 'work-order-assigned')].id"
        );

        assertThat(ids).isNotEmpty();

        mockMvc.perform(
                        patch(ALERTS + "/{alertId}/read", ids.get(0))
                                .header("Authorization",
                                        tokenFor("carla.garcia@fixcore.demo")))
                .andExpect(status().isNotFound());
    }

    private String alertsFor(String email) throws Exception {
        return mockMvc.perform(get(ALERTS).header("Authorization", tokenFor(email)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private String tokenFor(String email) throws Exception {
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"FixCore2026\"}".formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(response, "$.token");
    }
}
