package com.techmakers.fixcore.analytics.interfaces.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-assets.enabled=true",
        "fixcore.seed.demo-users.enabled=true", "fixcore.seed.demo-maintenance-plans.enabled=true",
        "fixcore.seed.demo-spare-parts.enabled=true", "fixcore.seed.demo-failures.enabled=true",
        "fixcore.seed.demo-work-orders.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalyticsControllerTests {

    private static final String ANALYTICS = "/api/v1/analytics";

    @Autowired
    private MockMvc mockMvc;

    private String plantManager;
    private String operationsManager;
    private String technician;
    private Integer ate;
    private Integer callao;

    @BeforeEach
    void setUp() throws Exception {
        plantManager = tokenFor("carla.garcia@fixcore.demo");
        operationsManager = tokenFor("victor.salazar@fixcore.demo");
        technician = tokenFor("jose.ramirez@fixcore.demo");
        ate = plantIdOf("Planta Ate");
        callao = plantIdOf("Planta Callao");
    }

    @Test
    void shouldScopeTheSummaryToThePlantManagerPlant() throws Exception {
        List<Integer> ateAssets = JsonPath.read(read("/api/v1/assets?plantId=" + ate), "$[*].id");
        mockMvc.perform(get(ANALYTICS + "/summary").header("Authorization", plantManager))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assets").value(ateAssets.size()))
                .andExpect(jsonPath("$.orders['in-progress']").isNumber())
                .andExpect(jsonPath("$.technicians").value(greaterThanOrEqualTo(2)));
        mockMvc.perform(get(ANALYTICS + "/summary").param("plantId", callao.toString()).header("Authorization", plantManager))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(ANALYTICS + "/summary").header("Authorization", technician))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGiveOperationsManagersEveryPlant() throws Exception {
        List<Integer> allAssets = JsonPath.read(read("/api/v1/assets"), "$[*].id");
        mockMvc.perform(get(ANALYTICS + "/summary").header("Authorization", operationsManager))
                .andExpect(jsonPath("$.assets").value(allAssets.size()));
        mockMvc.perform(get(ANALYTICS + "/summary").param("from", "2026-02-01").param("to", "2026-01-01")
                        .header("Authorization", operationsManager))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRankDowntimeAndReportTechnicianPerformance() throws Exception {
        var ranking = mockMvc.perform(get(ANALYTICS + "/downtime-ranking").header("Authorization", operationsManager))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<Double> hours = JsonPath.read(ranking, "$[*].hours");
        assertThat(hours).isNotEmpty().isSortedAccordingTo((a, b) -> Double.compare(b, a));

        Integer joseId = JsonPath.<List<Integer>>read(read("/api/v1/users?email=jose.ramirez@fixcore.demo"), "$[*].id").getFirst();
        mockMvc.perform(get(ANALYTICS + "/technicians/" + joseId + "/performance").header("Authorization", plantManager))
                .andExpect(jsonPath("$.technicianId").value(joseId))
                .andExpect(jsonPath("$.completed").value(greaterThanOrEqualTo(1)));
    }

    private Integer plantIdOf(String name) throws Exception {
        return JsonPath.<List<Integer>>read(read("/api/v1/plants"), "$[?(@.name == '%s')].id".formatted(name)).getFirst();
    }

    private String read(String url) throws Exception {
        return mockMvc.perform(get(url).header("Authorization", operationsManager)).andReturn().getResponse().getContentAsString();
    }

    private String tokenFor(String email) throws Exception {
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"FixCore2026\"}".formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(response, "$.token");
    }
}
