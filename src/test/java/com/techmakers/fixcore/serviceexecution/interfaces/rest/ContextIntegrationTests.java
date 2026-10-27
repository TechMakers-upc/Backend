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

import java.time.LocalDate;
import java.util.List;

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
class ContextIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private String plantManager;
    private String technician;
    private Integer plantId;
    private Integer technicianId;

    @BeforeEach
    void setUp() throws Exception {
        plantManager = tokenFor("carla.garcia@fixcore.demo");
        technician = tokenFor("jose.ramirez@fixcore.demo");
        plantId = idOf("/api/v1/assets", "$[?(@.code == 'BMB-05')].plantId");
        technicianId = idOf("/api/v1/users?email=jose.ramirez@fixcore.demo", "$[*].id");
    }

    @Test
    void shouldPropagateACorrectiveRepairAcrossContexts() throws Exception {
        Integer assetId = createAsset("INT-CORR");
        Integer failureId = JsonPath.read(perform(post("/api/v1/failures"), technician,
                "{\"assetId\":%d,\"kind\":\"leak\",\"description\":\"Paro\",\"lineStopped\":true}".formatted(assetId)), "$.id");
        expectAssetStatus(assetId, "down");

        String order = createOrder("corrective", assetId, failureId, null);
        mockMvc.perform(post(order + "/start").header("Authorization", technician)).andExpect(status().isOk());
        expectAssetStatus(assetId, "down");

        Integer bearing = idOf("/api/v1/spare-parts?plantId=" + plantId, "$[?(@.sku == 'RD-6205')].id");
        Integer stockBefore = idOf("/api/v1/spare-parts/" + bearing, "$.stock");
        perform(post(order + "/part-usages"), technician, "{\"partId\":%d,\"quantity\":2}".formatted(bearing));
        mockMvc.perform(get("/api/v1/spare-parts/" + bearing).header("Authorization", plantManager))
                .andExpect(jsonPath("$.stock").value(stockBefore - 2));
        Integer orderId = Integer.valueOf(order.substring(order.lastIndexOf('/') + 1));
        mockMvc.perform(get("/api/v1/stock-movements").param("partId", bearing.toString())
                        .header("Authorization", plantManager))
                .andExpect(jsonPath("$[?(@.workOrderId == %d && @.type == 'consumption')].quantity".formatted(orderId))
                        .value(-2));

        complete(order);
        expectAssetStatus(assetId, "operational");
        mockMvc.perform(get("/api/v1/failures/" + failureId).header("Authorization", plantManager))
                .andExpect(jsonPath("$.status").value("resolved"));
    }

    @Test
    void shouldRollThePlanForwardWhenAPreventiveOrderCloses() throws Exception {
        Integer assetId = createAsset("INT-PREV");
        Integer planId = JsonPath.read(perform(post("/api/v1/maintenance-plans"), plantManager, """
                {"assetId":%d,"title":"Lubricación","tasks":"Engrasar","frequencyDays":30,
                 "nextDueDate":"2026-12-01","technicianId":%d}""".formatted(assetId, technicianId)), "$.id");

        String order = createOrder("preventive", assetId, null, planId);
        mockMvc.perform(post(order + "/start").header("Authorization", technician)).andExpect(status().isOk());
        expectAssetStatus(assetId, "maintenance");

        complete(order);
        expectAssetStatus(assetId, "operational");
        mockMvc.perform(get("/api/v1/maintenance-plans/" + planId).header("Authorization", plantManager))
                .andExpect(jsonPath("$.lastCompletedAt").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.nextDueDate").value(LocalDate.now().plusDays(30).toString()));
    }

    private Integer createAsset(String code) throws Exception {
        return JsonPath.read(perform(post("/api/v1/assets"), plantManager, """
                {"plantId":%d,"code":"%s","name":"Equipo integrado","category":"mixer","criticality":"medium"}"""
                .formatted(plantId, code)), "$.id");
    }

    private String createOrder(String type, Integer assetId, Integer failureId, Integer planId) throws Exception {
        String created = perform(post("/api/v1/work-orders"), plantManager, """
                {"type":"%s","assetId":%d,"title":"Intervención","description":"","priority":"high",
                 "dueDate":"2026-12-01","technicianId":%d,"failureId":%s,"planId":%s}"""
                .formatted(type, assetId, technicianId, failureId, planId));
        return "/api/v1/work-orders/" + JsonPath.<Integer>read(created, "$.id");
    }

    private void complete(String order) throws Exception {
        mockMvc.perform(post(order + "/completion").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"solution\":\"Listo\"}"))
                .andExpect(jsonPath("$.status").value("completed"));
    }

    private void expectAssetStatus(Integer assetId, String expected) throws Exception {
        mockMvc.perform(get("/api/v1/assets/" + assetId).header("Authorization", plantManager))
                .andExpect(jsonPath("$.status").value(expected));
    }

    private String perform(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
                           String token, String body) throws Exception {
        return mockMvc.perform(request.header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
    }

    private Integer idOf(String url, String path) throws Exception {
        var json = mockMvc.perform(get(url).header("Authorization", plantManager)).andReturn().getResponse().getContentAsString();
        Object value = JsonPath.read(json, path);
        return value instanceof List<?> list ? (Integer) list.getFirst() : (Integer) value;
    }

    private String tokenFor(String email) throws Exception {
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"FixCore2026\"}".formatted(email)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(response, "$.token");
    }
}
