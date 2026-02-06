package com.raki.pos.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(AuthRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/data.sql") // Only loads Roles/Permissions
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Helper to create a user quickly
    private void createTestUser(String email, String password, Integer businessId) {
        String sql = """
            INSERT INTO users (name, email, password_hash, role_id, business_id, status)
            VALUES (?, ?, ?, (SELECT id FROM roles WHERE name = 'Employee'), ?, 'ACTIVE')
        """;
        jdbcTemplate.update(sql, "Test User", email, password, businessId);
    }

    // Helper to create a business and return its ID
    private int createTestBusiness(String name) {
        String sql = "INSERT INTO businesses (type, name, is_active) VALUES ('BAR', ?, 'OPEN')";
        jdbcTemplate.update(sql, name);
        return jdbcTemplate.queryForObject("SELECT id FROM businesses WHERE name = ?", Integer.class, name);
    }

    @Nested
    class FindUserByEmailAndPasswordTestsValidInputs {

        @Test
        void findUserByEmailAndPassword_WithExistingUser_ShouldReturnUserData() {
            // Arrange: specific data for this test
            createTestUser("test@test.com", "1234", null);

            // Act
            Map<String, Object> user = authRepository.findUserByEmailAndPassword("test@test.com", "1234");

            // Assert
            assertNotNull(user, "User should be found");
            assertEquals("Test User", user.get("name"));
            assertEquals("test@test.com", user.get("email"));
        }

        @Test
        void findUserByEmailAndPassword_WithNullBusinessId_ShouldReturnUser() {
            // Arrange
            createTestUser("freelancer@test.com", "1234", null);

            // Act
            Map<String, Object> user = authRepository.findUserByEmailAndPassword("freelancer@test.com", "1234");

            // Assert
            assertNotNull(user);
            assertNull(user.get("business_id"), "Business ID should be null");
        }

        @Test
        void findUserByEmailAndPassword_WithAssignedBusinessId_ShouldReturnUserWithBusiness() {
            // Arrange
            int businessId = createTestBusiness("The Tipsy Tavern");
            createTestUser("alice@tipsytavern.com", "secret_pass", businessId);

            // Act
            Map<String, Object> user = authRepository.findUserByEmailAndPassword("alice@tipsytavern.com", "secret_pass");

            // Assert
            assertNotNull(user);
            // Verify the ID matches what we just inserted
            assertEquals(businessId, ((Number) user.get("business_id")).intValue());
        }
    }

    @Nested
    class FindUserByEmailAndPasswordTestsInvalidInputs {

        @Test
        void findUserByEmailAndPassword_WithNonExistingUser_ShouldThrowException() {
            // No setup needed - empty DB regarding users

            BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> {
                authRepository.findUserByEmailAndPassword("ghost@test.com", "1234");
            });

            assertEquals("Authentication Failed", ex.getMessage());
        }

        @Test
        void findUserByEmailAndPassword_WithWrongPassword_ShouldThrowException() {
            // Arrange
            createTestUser("real@test.com", "correct_pass", null);

            // Act & Assert
            BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> {
                authRepository.findUserByEmailAndPassword("real@test.com", "wrong_pass");
            });

            assertEquals("Authentication Failed", ex.getMessage());
        }
    }
}