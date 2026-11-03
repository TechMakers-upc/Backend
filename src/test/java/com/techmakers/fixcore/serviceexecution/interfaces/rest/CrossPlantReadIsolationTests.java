package com.techmakers.fixcore.serviceexecution.interfaces.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "fixcore.seed.demo-plants.enabled=true",
        "fixcore.seed.demo-assets.enabled=true",
        "fixcore.seed.demo-users.enabled=true",
        "fixcore.seed.demo-maintenance-plans.enabled=true",
        "fixcore.seed.demo-spare-parts.enabled=true",
        "fixcore.seed.demo-stock-movements.enabled=true",
        "fixcore.seed.demo-failures.enabled=true"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrossPlantReadIsolationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void foreignAssetsFailuresAndPlansMustBeProtected() throws Exception {
        var operations = tokenFor("victor.salazar@fixcore.demo");
        var carla = tokenFor("carla.garcia@fixcore.demo");
        int callao = callaoId(operations);

        Integer asset = JsonPath.read(response("/api/v1/assets?plantId=" + callao, operations), "$[0].id");
        Integer failure = JsonPath.read(response("/api/v1/failures?plantId=" + callao, operations), "$[0].id");
        Integer plan = JsonPath.read(response("/api/v1/maintenance-plans?plantId=" + callao, operations), "$[0].id");

        mockMvc.perform(get("/api/v1/assets").param("plantId", String.valueOf(callao))
                        .header("Authorization", carla))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/assets/" + asset).header("Authorization", carla))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/failures").param("plantId", String.valueOf(callao))
                        .header("Authorization", carla))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/failures/" + failure).header("Authorization", carla))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/maintenance-plans").param("plantId", String.valueOf(callao))
                        .header("Authorization", carla))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/maintenance-plans/" + plan).header("Authorization", carla))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/assets").header("Authorization", carla))
                .andExpect(jsonPath("$[?(@.plantId == %d)]".formatted(callao)).isEmpty());
        mockMvc.perform(get("/api/v1/failures").header("Authorization", carla))
                .andExpect(jsonPath("$[?(@.plantId == %d)]".formatted(callao)).isEmpty());
        mockMvc.perform(get("/api/v1/maintenance-plans").header("Authorization", carla))
                .andExpect(jsonPath("$[?(@.plantId == %d)]".formatted(callao)).isEmpty());
    }

    @Test
    void foreignInventoryAndStockHistoryMustBeProtected() throws Exception {
        var operations = tokenFor("victor.salazar@fixcore.demo");
        var carla = tokenFor("carla.garcia@fixcore.demo");
        int callao = callaoId(operations);

        var foreignParts = response("/api/v1/spare-parts?plantId=" + callao, operations);
        Integer part = JsonPath.<List<Integer>>read(
                foreignParts, "$[?(@.sku == 'MRD-SVE')].id").getFirst();

        mockMvc.perform(get("/api/v1/spare-parts")
                        .param("plantId", String.valueOf(callao))
                        .header("Authorization", carla))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/spare-parts/" + part)
                        .header("Authorization", carla))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/spare-parts")
                        .param("plantId", String.valueOf(callao))
                        .param("belowMinimum", "true")
                        .header("Authorization", carla))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/stock-movements")
                        .param("plantId", String.valueOf(callao))
                        .header("Authorization", carla))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/stock-movements")
                        .param("partId", part.toString())
                        .header("Authorization", carla))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/api/v1/stock-movements")
                        .param("partId", part.toString())
                        .header("Authorization", operations))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    private int callaoId(String authorization) throws Exception {
        return JsonPath.<List<Integer>>read(response("/api/v1/plants", authorization),
                "$[?(@.name == 'Planta Callao')].id").getFirst();
    }

    private String response(String url, String authorization) throws Exception {
        return mockMvc.perform(get(url).header("Authorization", authorization))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String tokenFor(String email) throws Exception {
        var json = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"FixCore2026\"}".formatted(email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return "Bearer " + JsonPath.read(json, "$.token");
    }
}
