package com.techmakers.fixcore.iam.infrastructure.authorization.sfs.configuration;

import com.techmakers.fixcore.iam.application.outboundservices.hashing.HashingService;
import com.techmakers.fixcore.iam.application.outboundservices.tokens.TokenService;
import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import com.techmakers.fixcore.iam.domain.model.valueobjects.EmailAddress;
import com.techmakers.fixcore.iam.domain.model.valueobjects.FullName;
import com.techmakers.fixcore.iam.domain.model.valueobjects.Roles;
import com.techmakers.fixcore.iam.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebSecurityConfigurationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HashingService hashingService;

    @Autowired
    private TokenService tokenService;

    @Test
    void shouldRejectProtectedRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldRejectRequestsWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowApiDocumentationWithoutToken() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAuthenticateRequestsWithValidToken() throws Exception {
        userRepository.save(new User(new EmailAddress("tech@fixcore.com"), hashingService.encode("fixcore-2026"),
                new FullName("Carlos Quispe"), "Technician", 1L, Roles.TECHNICIAN));
        var token = tokenService.generateToken("tech@fixcore.com");

        mockMvc.perform(get("/api/v1/unknown-resource").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
