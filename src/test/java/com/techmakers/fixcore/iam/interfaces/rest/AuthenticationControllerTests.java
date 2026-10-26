package com.techmakers.fixcore.iam.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTests {

    private static final String SIGN_UP = "/api/v1/authentication/sign-up";
    private static final String SIGN_IN = "/api/v1/authentication/sign-in";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSignUpAndSignIn() throws Exception {
        mockMvc.perform(post(SIGN_UP).contentType(MediaType.APPLICATION_JSON).content(signUpBody("rosa@fixcore.com", "technician", "2")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/users/")))
                .andExpect(jsonPath("$.role").value("technician"))
                .andExpect(jsonPath("$.plantId").value(2))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post(SIGN_IN).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ROSA@fixcore.com\",\"password\":\"fixcore-2026\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.email").value("rosa@fixcore.com"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        var body = signUpBody("duplicate@fixcore.com", "plant-manager", "1");
        mockMvc.perform(post(SIGN_UP).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        mockMvc.perform(post(SIGN_UP).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isConflict());
    }

    @Test
    void shouldRejectTechnicianWithoutPlant() throws Exception {
        mockMvc.perform(post(SIGN_UP).contentType(MediaType.APPLICATION_JSON).content(signUpBody("noplant@fixcore.com", "technician", "null")))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectUnknownRole() throws Exception {
        mockMvc.perform(post(SIGN_UP).contentType(MediaType.APPLICATION_JSON).content(signUpBody("admin@fixcore.com", "admin", "1")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWrongPassword() throws Exception {
        mockMvc.perform(post(SIGN_UP).contentType(MediaType.APPLICATION_JSON).content(signUpBody("luis@fixcore.com", "technician", "3")))
                .andExpect(status().isCreated());

        mockMvc.perform(post(SIGN_IN).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"luis@fixcore.com\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email or password is incorrect"));
    }

    private String signUpBody(String email, String role, String plantId) {
        return """
                {"email":"%s","password":"fixcore-2026","fullName":"Test User","jobTitle":"Tester","plantId":%s,"role":"%s"}"""
                .formatted(email, plantId, role);
    }
}
