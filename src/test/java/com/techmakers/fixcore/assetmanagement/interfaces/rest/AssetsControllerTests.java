package com.techmakers.fixcore.assetmanagement.interfaces.rest;

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
        "fixcore.seed.demo-users.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AssetsControllerTests {

    private static final String ASSETS = "/api/v1/assets";

    @Autowired
    private MockMvc mockMvc;

    private String plantManager;
    private String technician;
    private String operationsManager;
    private int ate;
    private int callao;

    @BeforeEach
    void signIn() throws Exception {
        plantManager = tokenFor("carla.garcia@fixcore.demo");
        technician = tokenFor("jose.ramirez@fixcore.demo");
        operationsManager = tokenFor("victor.salazar@fixcore.demo");
        ate = plantIdOf("Planta Ate");
        callao = plantIdOf("Planta Callao");
    }

    @Test
    void shouldListSeededAssetsByPlant() throws Exception {
        mockMvc.perform(get(ASSETS).param("plantId", String.valueOf(ate)).header("Authorization", technician))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].plantId", everyItem(is(ate))))
                .andExpect(jsonPath("$[*].code", hasItem("CMP-01")))
                .andExpect(jsonPath("$[?(@.code == 'CMP-01')].status").value("down"));
    }

    @Test
    void shouldRegisterUpdateAndDeleteAssetAsPlantManager() throws Exception {
        var created = mockMvc.perform(post(ASSETS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "tst-01", "pump")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("TST-01"))
                .andExpect(jsonPath("$.status").value("operational"))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(created, "$.id");

        mockMvc.perform(put(ASSETS + "/" + id).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-01", "mixer")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("mixer"));

        mockMvc.perform(post(ASSETS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-01", "pump")))
                .andExpect(status().isConflict());

        mockMvc.perform(delete(ASSETS + "/" + id).header("Authorization", plantManager))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldChangeStatusAndManageManuals() throws Exception {
        var created = mockMvc.perform(post(ASSETS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-02", "press")))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(created, "$.id");

        mockMvc.perform(patch(ASSETS + "/" + id + "/status").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"maintenance\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("maintenance"));

        mockMvc.perform(post(ASSETS + "/" + id + "/manuals").header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Manual\",\"url\":\"https://fixcore.demo/m.pdf\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manuals[0].title").value("Manual"));

        mockMvc.perform(delete(ASSETS + "/" + id + "/manuals/0").header("Authorization", plantManager))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manuals").isEmpty());
    }

    @Test
    void shouldRejectInvalidRequestsAndRoles() throws Exception {
        mockMvc.perform(post(ASSETS).header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-03", "pump")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post(ASSETS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-04", "rocket")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(ASSETS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(999, "TST-05", "pump")))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/plants/" + ate).header("Authorization", operationsManager))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldKeepPlantManagersInsideTheirPlant() throws Exception {
        mockMvc.perform(post(ASSETS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(callao, "TST-90", "pump")))
                .andExpect(status().isForbidden());
        var callaoAsset = mockMvc.perform(get(ASSETS).param("plantId", String.valueOf(callao)).header("Authorization", technician))
                .andReturn().getResponse().getContentAsString();
        Integer assetId = JsonPath.<List<Integer>>read(callaoAsset, "$[*].id").getFirst();
        mockMvc.perform(delete(ASSETS + "/" + assetId).header("Authorization", plantManager))
                .andExpect(status().isForbidden());
    }

    private String body(int plantId, String code, String category) {
        return """
                {"plantId":%d,"code":"%s","name":"Equipo de prueba","category":"%s","criticality":"medium",
                 "location":"Línea 1","brand":"FixCore","model":"T-1","serialNumber":"SN-1","installedAt":"2024-02-10"}"""
                .formatted(plantId, code, category);
    }

    private int plantIdOf(String name) throws Exception {
        var plants = mockMvc.perform(get("/api/v1/plants").header("Authorization", technician))
                .andReturn().getResponse().getContentAsString();
        List<Integer> ids = JsonPath.read(plants, "$[?(@.name == '%s')].id".formatted(name));
        return ids.getFirst();
    }

    private String tokenFor(String email) throws Exception {
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"FixCore2026\"}".formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(response, "$.token");
    }
}
