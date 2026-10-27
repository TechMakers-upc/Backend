package com.techmakers.fixcore.inventory.interfaces.rest;

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
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-users.enabled=true",
        "fixcore.seed.demo-spare-parts.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SparePartsControllerTests {

    private static final String PARTS = "/api/v1/spare-parts";

    @Autowired
    private MockMvc mockMvc;

    private String plantManager;
    private String technician;
    private Integer ate;

    @BeforeEach
    void setUp() throws Exception {
        plantManager = tokenFor("carla.garcia@fixcore.demo");
        technician = tokenFor("jose.ramirez@fixcore.demo");
        var plants = mockMvc.perform(get("/api/v1/plants").header("Authorization", technician))
                .andReturn().getResponse().getContentAsString();
        ate = JsonPath.<List<Integer>>read(plants, "$[?(@.name == 'Planta Ate')].id").getFirst();
    }

    @Test
    void shouldListSeededPartsAndThoseBelowMinimum() throws Exception {
        mockMvc.perform(get(PARTS).param("plantId", ate.toString()).header("Authorization", technician))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sku", hasItem("RD-6205")))
                .andExpect(jsonPath("$[?(@.sku == 'RD-6205')].level").value("ok"));
        mockMvc.perform(get(PARTS).param("belowMinimum", "true").header("Authorization", technician))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].level", everyItem(not("ok"))));
    }

    @Test
    void shouldRegisterUpdateAndDeletePart() throws Exception {
        var created = mockMvc.perform(post(PARTS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "tst-01", "kit")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("TST-01"))
                .andExpect(jsonPath("$.stock").value(0))
                .andExpect(jsonPath("$.level").value("out"))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(created, "$.id");

        mockMvc.perform(put(PARTS + "/" + id).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-01", "liter")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unit").value("liter"));
        mockMvc.perform(post(PARTS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-01", "kit")))
                .andExpect(status().isConflict());
        mockMvc.perform(delete(PARTS + "/" + id).header("Authorization", plantManager))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectInvalidRequestsAndRoles() throws Exception {
        mockMvc.perform(post(PARTS).header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-02", "kit")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post(PARTS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(999999, "TST-03", "kit")))
                .andExpect(status().isNotFound());
        mockMvc.perform(post(PARTS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(ate, "TST-04", "gallon")))
                .andExpect(status().isBadRequest());
    }

    private String body(Integer plantId, String sku, String unit) {
        return """
                {"plantId":%d,"sku":"%s","name":"Repuesto de prueba","category":"consumable","unit":"%s",
                 "minStock":3,"location":"Z-01"}"""
                .formatted(plantId, sku, unit);
    }

    private String tokenFor(String email) throws Exception {
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"FixCore2026\"}".formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(response, "$.token");
    }
}
