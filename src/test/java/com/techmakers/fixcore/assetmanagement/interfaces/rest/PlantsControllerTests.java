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

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-users.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantsControllerTests {

    private static final String PLANTS = "/api/v1/plants";

    @Autowired
    private MockMvc mockMvc;

    private String operationsManager;
    private String plantManager;

    @BeforeEach
    void signIn() throws Exception {
        operationsManager = tokenFor("victor.salazar@fixcore.demo");
        plantManager = tokenFor("carla.garcia@fixcore.demo");
    }

    @Test
    void shouldListSeededPlantsForAnyAuthenticatedUser() throws Exception {
        mockMvc.perform(get(PLANTS).header("Authorization", plantManager))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItems("Planta Ate", "Planta Callao", "Planta Arequipa")))
                .andExpect(jsonPath("$[?(@.name == 'Planta Ate')].displayName").value("Planta Ate · Lima"));
    }

    @Test
    void shouldCreateUpdateAndDeletePlantAsOperationsManager() throws Exception {
        var created = mockMvc.perform(post(PLANTS).header("Authorization", operationsManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body("Planta Trujillo", "Trujillo")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(created, "$.id");

        mockMvc.perform(put(PLANTS + "/" + id).header("Authorization", operationsManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body("Planta Trujillo Norte", "Trujillo")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Planta Trujillo Norte"));

        mockMvc.perform(delete(PLANTS + "/" + id).header("Authorization", operationsManager))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(PLANTS + "/" + id).header("Authorization", operationsManager))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldForbidPlantChangesForPlantManagers() throws Exception {
        mockMvc.perform(post(PLANTS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body("Planta Piura", "Piura")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void shouldRejectDuplicateAndInvalidPlants() throws Exception {
        mockMvc.perform(post(PLANTS).header("Authorization", operationsManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body("planta ate", "Lima")))
                .andExpect(status().isConflict());
        mockMvc.perform(post(PLANTS).header("Authorization", operationsManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body("", "")))
                .andExpect(status().isBadRequest());
    }

    private String body(String name, String city) {
        return "{\"name\":\"%s\",\"city\":\"%s\",\"address\":\"Av. Industrial 100\"}".formatted(name, city);
    }

    private String tokenFor(String email) throws Exception {
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"FixCore2026\"}".formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(response, "$.token");
    }
}
