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

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-assets.enabled=true",
        "fixcore.seed.demo-users.enabled=true", "fixcore.seed.demo-maintenance-plans.enabled=true",
        "fixcore.seed.demo-spare-parts.enabled=true", "fixcore.seed.demo-failures.enabled=true",
        "fixcore.seed.demo-work-orders.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkOrdersControllerTests {

    private static final String ORDERS = "/api/v1/work-orders";

    @Autowired
    private MockMvc mockMvc;

    private String plantManager;
    private String technician;
    private Integer pumpId;
    private Integer technicianId;
    private Integer plantManagerId;

    @BeforeEach
    void setUp() throws Exception {
        plantManager = tokenFor("carla.garcia@fixcore.demo");
        technician = tokenFor("jose.ramirez@fixcore.demo");
        pumpId = idOf("/api/v1/assets", "$[?(@.code == 'BMB-05')].id");
        technicianId = idOf("/api/v1/users?email=jose.ramirez@fixcore.demo", "$[*].id");
        plantManagerId = idOf("/api/v1/users?email=carla.garcia@fixcore.demo", "$[*].id");
    }

    @Test
    void shouldExposeSeededOrdersLinkedToFailuresAndParts() throws Exception {
        Integer first = idOf(ORDERS, "$[?(@.code == 'OT-2026-0001')].id");
        mockMvc.perform(get(ORDERS + "/" + first).header("Authorization", technician))
                .andExpect(jsonPath("$.status").value("completed"))
                .andExpect(jsonPath("$.partsUsed[0].quantity").value(2))
                .andExpect(jsonPath("$.repairHours").isNumber())
                .andExpect(jsonPath("$.failureId").isNumber());
        mockMvc.perform(get(ORDERS).param("technicianId", technicianId.toString()).header("Authorization", technician))
                .andExpect(jsonPath("$[0].status").value("pending"));
    }

    @Test
    void shouldRunTheFullMaintenanceFlow() throws Exception {
        var failure = mockMvc.perform(post("/api/v1/failures").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"assetId\":%d,\"kind\":\"leak\",\"description\":\"Fuga\",\"lineStopped\":false}".formatted(pumpId)))
                .andReturn().getResponse().getContentAsString();
        Integer failureId = JsonPath.read(failure, "$.id");

        var created = mockMvc.perform(post(ORDERS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(pumpId, failureId, null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(startsWith("OT-")))
                .andReturn().getResponse().getContentAsString();
        String order = ORDERS + "/" + JsonPath.<Integer>read(created, "$.id");
        mockMvc.perform(get("/api/v1/failures/" + failureId).header("Authorization", plantManager))
                .andExpect(jsonPath("$.status").value("in-progress"));

        mockMvc.perform(post(order + "/start").header("Authorization", technician)).andExpect(status().isConflict());
        mockMvc.perform(patch(order + "/assignment").header("Authorization", plantManager)
                .contentType(MediaType.APPLICATION_JSON).content("{\"technicianId\":%d}".formatted(technicianId)));
        mockMvc.perform(post(order + "/start").header("Authorization", technician))
                .andExpect(jsonPath("$.status").value("in-progress"));
        mockMvc.perform(post(order + "/work-logs").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"note\":\"Se cambió el sello\"}"))
                .andExpect(jsonPath("$.workLog[0].authorId").value(technicianId));
        Integer bearing = idOf("/api/v1/spare-parts?plantId=" + plantOfPump(), "$[?(@.sku == 'RD-6205')].id");
        mockMvc.perform(post(order + "/part-usages").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"partId\":%d,\"quantity\":1}".formatted(bearing)))
                .andExpect(jsonPath("$.partsUsed[0].partId").value(bearing));
        mockMvc.perform(post(order + "/completion").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"solution\":\"Sello reemplazado\"}"))
                .andExpect(jsonPath("$.status").value("completed"));
        mockMvc.perform(get("/api/v1/failures/" + failureId).header("Authorization", plantManager))
                .andExpect(jsonPath("$.status").value("resolved"));
    }

    @Test
    void shouldEnforceWorkOrderRules() throws Exception {
        mockMvc.perform(post(ORDERS).header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content(body(pumpId, null, null)))
                .andExpect(status().isForbidden());
        mockMvc.perform(post(ORDERS).header("Authorization", plantManager)
                        .contentType(MediaType.APPLICATION_JSON).content(body(pumpId, null, plantManagerId)))
                .andExpect(status().isConflict());
        Integer completed = idOf(ORDERS, "$[?(@.code == 'OT-2026-0001')].id");
        mockMvc.perform(delete(ORDERS + "/" + completed).header("Authorization", plantManager))
                .andExpect(status().isConflict());
        Integer callaoPart = idOf("/api/v1/spare-parts", "$[?(@.sku == 'MRD-SVE')].id");
        Integer inProgress = idOf(ORDERS, "$[?(@.code == 'OT-2026-0010')].id");
        mockMvc.perform(post(ORDERS + "/" + inProgress + "/part-usages").header("Authorization", technician)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"partId\":%d,\"quantity\":1}".formatted(callaoPart)))
                .andExpect(status().isConflict());
    }

    private Integer plantOfPump() throws Exception {
        return idOf("/api/v1/assets/" + pumpId, "$.plantId");
    }

    private String body(Integer assetId, Integer failureId, Integer technicianId) {
        return """
                {"type":"corrective","assetId":%d,"title":"Reparar fuga","description":"","priority":"high",
                 "dueDate":"2026-12-01","technicianId":%s,"failureId":%s}""".formatted(assetId, technicianId, failureId);
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
