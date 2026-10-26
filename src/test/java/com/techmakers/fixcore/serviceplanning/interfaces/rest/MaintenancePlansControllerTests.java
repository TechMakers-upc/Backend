package com.techmakers.fixcore.serviceplanning.interfaces.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-assets.enabled=true",
        "fixcore.seed.demo-users.enabled=true", "fixcore.seed.demo-maintenance-plans.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MaintenancePlansControllerTests {

    private static final String PLANS = "/api/v1/maintenance-plans";

    @Autowired
    private MockMvc mockMvc;

    private String plantManager;
    private String technician;
    private Integer compressorId;
    private Integer technicianId;
    private Integer plantManagerId;

    @BeforeEach
    void setUp() throws Exception {
        plantManager = tokenFor("carla.garcia@fixcore.demo");
        technician = tokenFor("jose.ramirez@fixcore.demo");
        var assets = read("/api/v1/assets");
        compressorId = JsonPath.<List<Integer>>read(assets, "$[?(@.code == 'CMP-01')].id").getFirst();
        technicianId = JsonPath.<List<Integer>>read(read("/api/v1/users?email=jose.ramirez@fixcore.demo"), "$[*].id").getFirst();
        plantManagerId = JsonPath.<List<Integer>>read(read("/api/v1/users?email=carla.garcia@fixcore.demo"), "$[*].id").getFirst();
    }

    @Test
    void shouldListSeededPlansForTheAsset() throws Exception {
        mockMvc.perform(get(PLANS).param("assetId", compressorId.toString()).header("Authorization", technician))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Cambio de filtros y aceite")))
                .andExpect(jsonPath("$[0].standing").isNotEmpty());
    }

    @Test
    void shouldScheduleAndManagePlanLifecycle() throws Exception {
        var created = mockMvc.perform(post(PLANS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(compressorId, 30, technicianId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(created, "$.id");
        var plan = PLANS + "/" + id;

        mockMvc.perform(post(plan + "/suspension").header("Authorization", plantManager))
                .andExpect(jsonPath("$.standing").value("suspended"));
        mockMvc.perform(post(plan + "/reactivation").header("Authorization", plantManager))
                .andExpect(jsonPath("$.active").value(true));
        mockMvc.perform(post(plan + "/completions").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"completedOn\":\"2026-10-20\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastCompletedAt").value("2026-10-20"))
                .andExpect(jsonPath("$.nextDueDate").value("2026-11-19"));
        mockMvc.perform(patch(plan + "/schedule").header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nextDueDate\":\"2026-12-01\"}"))
                .andExpect(jsonPath("$.nextDueDate").value("2026-12-01"));
        mockMvc.perform(delete(plan).header("Authorization", plantManager))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectInvalidPlans() throws Exception {
        mockMvc.perform(post(PLANS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(compressorId, 30, plantManagerId)))
                .andExpect(status().isConflict());
        mockMvc.perform(post(PLANS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(999999, 30, null)))
                .andExpect(status().isNotFound());
        mockMvc.perform(post(PLANS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(compressorId, 0, null)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(PLANS).header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content(body(compressorId, 30, null)))
                .andExpect(status().isForbidden());
    }

    private String body(Integer assetId, int frequencyDays, Integer technician) {
        return """
                {"assetId":%d,"title":"Inspección de prueba","tasks":"Revisar","frequencyDays":%d,
                 "nextDueDate":"%s","technicianId":%s}"""
                .formatted(assetId, frequencyDays, LocalDate.now().plusDays(30), technician);
    }

    private String read(String url) throws Exception {
        return mockMvc.perform(get(url).header("Authorization", plantManager)).andReturn().getResponse().getContentAsString();
    }

    private String tokenFor(String email) throws Exception {
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"FixCore2026\"}".formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(response, "$.token");
    }
}
