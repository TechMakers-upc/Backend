package com.techmakers.fixcore.iam.interfaces.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"fixcore.seed.demo-plants.enabled=true", "fixcore.seed.demo-users.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsersControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    void signInAsDemoUser() throws Exception {
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"carla.garcia@fixcore.demo\",\"password\":\"FixCore2026\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = "Bearer " + JsonPath.read(response, "$.token");
    }

    @Test
    void shouldListSeededUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))));
    }

    @Test
    void shouldFindUserByEmail() throws Exception {
        mockMvc.perform(get("/api/v1/users").param("email", "victor.salazar@fixcore.demo").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].role").value("operations-manager"))
                .andExpect(jsonPath("$[0].plantId").doesNotExist());
    }

    @Test
    void shouldReturnCurrentUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/me").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Carla García"))
                .andExpect(jsonPath("$.role").value("plant-manager"));
    }

    @Test
    void shouldReturnNotFoundForUnknownUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/999999").header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListRoles() throws Exception {
        mockMvc.perform(get("/api/v1/roles").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].value").value("operations-manager"))
                .andExpect(jsonPath("$[1].requiresPlant").value(false));
    }
}
