package com.raki.pos.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Best Design Principle: Test Isolation.
     * We clean up and set up fresh data before every single test case.
     */
    @BeforeEach
    void setUp() {
        // Clean up: Delete the specific test user if they exist to avoid unique constraint errors
        jdbcTemplate.update("DELETE FROM users WHERE email = ?", "integration@school.com");

        // Insert a fresh user for this specific test
        // Bypass the Service layer here and write directly to DB to ensure the "Start State" is correct.
        String sql = """
            INSERT INTO users (name, email, password_hash, role_id, status)
            VALUES (?, ?, ?, (SELECT id FROM roles WHERE name = 'Employee'), 'ACTIVE')
        """;

        jdbcTemplate.update(sql, "Integration User", "integration@school.com", "1234");
    }

    @Test
    void login_EndToEnd_ShouldReturnToken() throws Exception {
        // Arrange
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail("integration@school.com");
        request.setPassword("1234");

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username").value("Integration User"));
    }

    @Test
    void login_WithWrongPassword_ShouldFail_EndToEnd() throws Exception {
        // Arrange
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail("integration@school.com");
        request.setPassword("wrong-password");

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Failed"));
    }
}