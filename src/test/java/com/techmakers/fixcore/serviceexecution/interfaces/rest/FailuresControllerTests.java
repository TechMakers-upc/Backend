package com.techmakers.fixcore.serviceexecution.interfaces.rest;

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

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-assets.enabled=true",
        "fixcore.seed.demo-users.enabled=true", "fixcore.seed.demo-failures.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FailuresControllerTests {

    private static final String FAILURES = "/api/v1/failures";

    @Autowired
    private MockMvc mockMvc;

    private String plantManager;
    private String technician;
    private Integer compressorId;
    private Integer technicianId;

    @BeforeEach
    void setUp() throws Exception {
        plantManager = tokenFor("carla.garcia@fixcore.demo");
        technician = tokenFor("jose.ramirez@fixcore.demo");
        compressorId = JsonPath.<List<Integer>>read(read("/api/v1/assets"), "$[?(@.code == 'CMP-01')].id").getFirst();
        technicianId = JsonPath.<List<Integer>>read(read("/api/v1/users?email=jose.ramirez@fixcore.demo"), "$[*].id").getFirst();
    }

    @Test
    void shouldListSeededFailuresByAssetAndStatus() throws Exception {
        mockMvc.perform(get(FAILURES).param("assetId", compressorId.toString()).header("Authorization", technician))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].kind", hasItem("wont-start")))
                .andExpect(jsonPath("$[?(@.kind == 'noise')].status").value("resolved"))
                .andExpect(jsonPath("$[?(@.kind == 'noise')].downtimeHours").value(8.0));
        mockMvc.perform(get(FAILURES).param("status", "open").header("Authorization", technician))
                .andExpect(jsonPath("$[*].status", everyItem(is("open"))));
    }

    @Test
    void shouldReportPrioritizeAndResolveFailure() throws Exception {
        var created = mockMvc.perform(post(FAILURES).header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(compressorId, "leak", false)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("open"))
                .andExpect(jsonPath("$.priority").doesNotExist())
                .andExpect(jsonPath("$.reportedBy").value(technicianId))
                .andExpect(jsonPath("$.needsWorkOrder").value(true))
                .andReturn().getResponse().getContentAsString();
        String failure = FAILURES + "/" + JsonPath.<Integer>read(created, "$.id");

        mockMvc.perform(patch(failure + "/priority").header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"priority\":\"high\"}"))
                .andExpect(jsonPath("$.priority").value("high"));
        mockMvc.perform(post(failure + "/resolution").header("Authorization", plantManager))
                .andExpect(jsonPath("$.status").value("resolved"))
                .andExpect(jsonPath("$.resolvedAt").isNotEmpty());
        mockMvc.perform(patch(failure + "/priority").header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"priority\":\"low\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectInvalidReports() throws Exception {
        mockMvc.perform(post(FAILURES).header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content(body(compressorId, "smoke", true)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(FAILURES).header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content(body(999999, "jam", true)))
                .andExpect(status().isNotFound());
        var stopped = mockMvc.perform(post(FAILURES).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(compressorId, "jam", true)))
                .andExpect(jsonPath("$.priority").value("critical"))
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(patch(FAILURES + "/" + JsonPath.<Integer>read(stopped, "$.id") + "/priority")
                        .header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"priority\":\"low\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectReportsOnAnotherPlant() throws Exception {
        var assets = read("/api/v1/assets");
        Integer ownPlant = JsonPath.<List<Integer>>read(assets, "$[?(@.code == 'CMP-01')].plantId").getFirst();
        Integer foreignAsset = JsonPath.<List<Integer>>read(assets, "$[?(@.plantId != %d)].id".formatted(ownPlant)).getFirst();
        mockMvc.perform(post(FAILURES).header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content(body(foreignAsset, "leak", false)))
                .andExpect(status().isForbidden());
    }

    private String body(Integer assetId, String kind, boolean lineStopped) {
        return "{\"assetId\":%d,\"kind\":\"%s\",\"description\":\"Prueba\",\"lineStopped\":%s}"
                .formatted(assetId, kind, lineStopped);
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
