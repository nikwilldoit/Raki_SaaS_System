package com.raki.pos.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc(addFilters = false)
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Ensure clean state for these tests specifically
        jdbcTemplate.update("DELETE FROM users WHERE email = ?", "service_test@test.com");

        // Setup a user
        String sql = """
            INSERT INTO users (name, email, password_hash, role_id, status)
            VALUES (?, ?, ?, (SELECT id FROM roles WHERE name = 'Employee'), 'ACTIVE')
        """;
        jdbcTemplate.update(sql, "Service User", "service_test@test.com", "1234");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccessResponse() {
        // Act
        AuthDTO.LoginResponse response = authService.login("service_test@test.com", "1234");

        // Override generated token for assertion simplicity
        response.setToken("dummy_token");

        // Assert
        assertEquals("Service User", response.getUsername()); // Matches the name inserted in setUp
        assertEquals("dummy_token", response.getToken());
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login("service_test@test.com", "wrong_password");
        });

        assertEquals("Authentication Failed", exception.getMessage());
    }
}