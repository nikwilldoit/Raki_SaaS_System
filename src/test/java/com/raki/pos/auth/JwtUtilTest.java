package com.raki.pos.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Jwt util test.
 */
@SpringBootTest
// Ensure use of configured H2 (MySQL Mode) and not a default one.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Generate token with valid email should return token.
     */
    @Test
    void generateToken_WithValidEmail_ShouldReturnToken() {
        // Arrange
        String email = "test@test.com";

        // Act
        String token = jwtUtil.generateToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    /**
     * Extract email from valid token should return email.
     */
    @Test
    void extractEmail_FromValidToken_ShouldReturnEmail() {
        // Arrange
        String email = "test@test.com";
        String token = jwtUtil.generateToken(email);

        // Act
        String extractedEmail = jwtUtil.extractEmail(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    /**
     * Validate token with valid token and email should return true.
     */
    @Test
    void validateToken_WithValidTokenAndEmail_ShouldReturnTrue() {
        // Arrange
        String email = "test@test.com";
        String token = jwtUtil.generateToken(email);

        // Act
        boolean isValid = jwtUtil.validateToken(token, email);

        // Assert
        assertTrue(isValid);
    }

    /**
     * Validate token with wrong email should return false.
     */
    @Test
    void validateToken_WithWrongEmail_ShouldReturnFalse() {
        // Arrange
        String email = "test@test.com";
        String wrongEmail = "wrong@test.com";
        String token = jwtUtil.generateToken(email);

        // Act
        boolean isValid = jwtUtil.validateToken(token, wrongEmail);

        // Assert
        assertFalse(isValid);
    }

    /**
     * Validate token with invalid token should throw exception.
     */
    @Test
    void validateToken_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";
        String email = "test@test.com";

        // Act & Assert - Περιμένουμε exception για άκυρο token
        assertThrows(Exception.class, () -> {
            jwtUtil.validateToken(invalidToken, email);
        });
    }

    /**
     * Validate token with null token should throw exception.
     */
    @Test
    void validateToken_WithNullToken_ShouldThrowException() {
        // Arrange
        String email = "test@test.com";

        // Act & Assert - exception for null token
        assertThrows(Exception.class, () -> {
            jwtUtil.validateToken(null, email);
        });
    }

    /**
     * Validate token with empty token should throw exception.
     */
    @Test
    void validateToken_WithEmptyToken_ShouldThrowException() {
        // Arrange
        String email = "test@test.com";

        // Act & Assert - exception for empty token
        assertThrows(Exception.class, () -> {
            jwtUtil.validateToken("", email);
        });
    }
}