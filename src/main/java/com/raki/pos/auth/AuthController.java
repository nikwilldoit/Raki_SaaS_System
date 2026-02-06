package com.raki.pos.auth;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class responsible for handling authentication-related HTTP requests.
 */
@RestController
@RequestMapping() // All URLs start from the root
@CrossOrigin(origins = "http://localhost:3000") // Allows cross-origin requests from React (running on localhost:3000)
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Constructor for AuthController.
     *
     * @param authService the service used for authentication operations
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user based on the provided login credentials.
     * <p>
     * This method validates the request body against the constraints defined in
     * {@link AuthDTO.LoginRequest}. If validation fails, the request is rejected
     * before the method body executes.
     *
     * @param loginRequest The DTO containing the user's email and password.
     *                     Must be valid according to annotations in the class.
     * @return A {@link ResponseEntity} containing the {@link AuthDTO.LoginResponse}
     * with success status and user details.
     *
     * @throws org.springframework.web.bind.MethodArgumentNotValidException if the
     * {@code loginRequest} fails validation checks (e.g., invalid email format).
     *
     * @throws org.springframework.security.authentication.BadCredentialsException if
     * the provided credentials are incorrect.
     */
    @PostMapping("/api/login")
    public ResponseEntity<AuthDTO.LoginResponse> login(@Valid @RequestBody AuthDTO.LoginRequest loginRequest) {

        logger.info("POST /api/login request for {}", loginRequest.getEmail());
        logger.debug("Request body: {}", loginRequest.toString());

        // Takes the data from React (JSON → Java Object)
        AuthDTO.LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        logger.info("Login successful for {}", loginRequest.getEmail());
        return ResponseEntity.ok(response); // Returns a 200 OK response with the login result

    }

    /**
     * Handles GET requests to the /status endpoint.
     * Provides a simple health check for the authentication endpoint.
     *
     * @return a string indicating the status of the authentication endpoint
     */
    @GetMapping("/api/status") // GET request for health check
    public String status() {
        return "Auth endpoint is working!"; // Simple response string
    }
}