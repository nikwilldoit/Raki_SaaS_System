package com.raki.pos.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service class responsible for handling authentication-related operations.
 */
@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;

    /**
     * Constructor for AuthService.
     *
     * @param authRepository the repository used for authentication operations
     * @param jwtUtil the utility class used for generating and validating JWT tokens
     */
    public AuthService(AuthRepository authRepository, JwtUtil jwtUtil) {
        this.authRepository = authRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user based on the provided email and password.
     * <p>
     * This method retrieves the user details from the database using the provided
     * email and password. If the authentication is successful, it generates a JWT
     * token for the user and returns a response containing the user's name and the token.
     *
     * @param email the email of the user attempting to log in
     * @param password the password of the user attempting to log in
     * @return an {@link AuthDTO.LoginResponse} object containing the authentication result,
     * the user's name, and the generated JWT token
     */
    public AuthDTO.LoginResponse login(String email, String password) {

        logger.info("Login attempt for {}", email);

        // Check if user exists
        Map<String, Object> user = authRepository.findUserByEmailAndPassword(email, password);

        String username = (String) user.get("name");

        logger.info("Login SUCCESS for {}", email);

        // Generate JWT Token based on email
        String token = jwtUtil.generateToken(email);

        logger.debug("Generated token for {}: {}", email, token);

        return new AuthDTO.LoginResponse(username, token);
    }
}