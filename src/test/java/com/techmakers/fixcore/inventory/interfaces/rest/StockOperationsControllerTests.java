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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-users.enabled=true",
        "fixcore.seed.demo-spare-parts.enabled=true", "fixcore.seed.demo-stock-movements.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StockOperationsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private String plantManager;
    private String technician;
    private Integer ate;
    private Integer carlaId;

    @BeforeEach
    void setUp() throws Exception {
        plantManager = tokenFor("carla.garcia@fixcore.demo");
        technician = tokenFor("jose.ramirez@fixcore.demo");
        ate = JsonPath.<List<Integer>>read(read("/api/v1/plants"), "$[?(@.name == 'Planta Ate')].id").getFirst();
        carlaId = JsonPath.<List<Integer>>read(read("/api/v1/users?email=carla.garcia@fixcore.demo"), "$[*].id").getFirst();
    }

    @Test
    void shouldExposeSeededHistoryNewestFirst() throws Exception {
        var parts = read("/api/v1/spare-parts?plantId=" + ate);
        Integer bearing = JsonPath.<List<Integer>>read(parts, "$[?(@.sku == 'RD-6205')].id").getFirst();

        mockMvc.perform(get("/api/v1/stock-movements").param("partId", bearing.toString()).header("Authorization", technician))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("consumption"))
                .andExpect(jsonPath("$[0].stockAfter").value(14))
                .andExpect(jsonPath("$[1].type").value("entry"));
    }

    @Test
    void shouldRecordEntriesConsumptionsAndAdjustments() throws Exception {
        var part = "/api/v1/spare-parts/" + createPart("TST-STK", 5);
        mockMvc.perform(get("/api/v1/stock-movements").param("partId", part.substring(part.lastIndexOf('/') + 1))
                        .header("Authorization", plantManager))
                .andExpect(jsonPath("$[0].reason").value("initial-stock"))
                .andExpect(jsonPath("$[0].userId").value(carlaId));

        mockMvc.perform(post(part + "/entries").header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":10,\"reason\":\"OC-900\"}"))
                .andExpect(jsonPath("$.stock").value(15));
        mockMvc.perform(post(part + "/consumptions").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":4,\"workOrderId\":77,\"reason\":\"OT\"}"))
                .andExpect(jsonPath("$.stock").value(11));
        mockMvc.perform(post(part + "/adjustments").header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"countedStock\":9,\"reason\":\"Conteo físico\"}"))
                .andExpect(jsonPath("$.stock").value(9));

        mockMvc.perform(get("/api/v1/stock-movements").param("plantId", ate.toString()).header("Authorization", plantManager))
                .andExpect(jsonPath("$[*].workOrderId", hasItem(77)));
    }

    @Test
    void shouldRejectInvalidStockOperations() throws Exception {
        var part = "/api/v1/spare-parts/" + createPart("TST-ERR", 2);

        mockMvc.perform(post(part + "/consumptions").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":3}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post(part + "/adjustments").header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"countedStock\":1,\"reason\":\"\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(part + "/entries").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":1}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete(part).header("Authorization", plantManager))
                .andExpect(status().isConflict());
    }

    private Integer createPart(String sku, int initialStock) throws Exception {
        var created = mockMvc.perform(post("/api/v1/spare-parts").header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"plantId":%d,"sku":"%s","name":"Repuesto","category":"mechanical","unit":"unit",
                                 "minStock":1,"location":"Z-02","initialStock":%d}""".formatted(ate, sku, initialStock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stock").value(initialStock))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(created, "$.id");
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
