package com.raki.pos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for the application.
 * <p>
 * This class intercepts exceptions thrown by any {@link org.springframework.web.bind.annotation.RestController}
 * in the application and transforms them into standard JSON error responses.
 * It ensures that the API clients receive consistent error formats.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation exceptions thrown when {@code @Valid} checks fail on controller arguments.
     * <p>
     * This method captures the {@link MethodArgumentNotValidException}, iterates through all
     * validation errors, and constructs a simple map where the key is the field name
     * (e.g., "email") and the value is the error message (e.g., "must be a valid email").
     *
     * @param ex The exception instance containing the list of validation violations.
     * @return A {@link ResponseEntity} containing a Map of field errors and the
     * {@link HttpStatus#BAD_REQUEST} status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        // Collects all field errors (e.g., "email": "Please provide a valid email address")
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors); //400
    }

    /**
     * Handles authentication errors such as invalid credentials.
     * <p>
     * This method captures {@link BadCredentialsException} and returns a response
     * with an error message and HTTP status 401 (Unauthorized).
     *
     * @param ex The exception instance containing authentication failure details.
     * @return A {@link ResponseEntity} containing the error message and the
     * {@link HttpStatus#UNAUTHORIZED} status.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleAuthErrors(BadCredentialsException ex) {
        logger.info("Authentication failed: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse); // 401
    }

    /**
     * Handles database-related exceptions such as connection issues or constraint violations.
     * <p>
     * This method captures {@link DataAccessException}, logs the error details, and returns
     * a generic error message to avoid exposing sensitive database information.
     *
     * @param ex The exception instance containing database error details.
     * @return A {@link ResponseEntity} containing a generic error message and the
     * {@link HttpStatus#INTERNAL_SERVER_ERROR} status.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseErrors(DataAccessException ex) {
        // Log the full stack trace internally for debugging
        logger.error("Database error occurred: ", ex);

        Map<String, String> errorResponse = new HashMap<>();
        // generic message for security
        errorResponse.put("error", "A database error occurred. Please check your request or contact support.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); //500
    }

    /**
     * Handles any other unexpected exceptions that are not explicitly handled.
     * <p>
     * This method captures generic {@link Exception}, logs the error details, and returns
     * a generic error message to the client.
     *
     * @param ex The exception instance containing error details.
     * @return A {@link ResponseEntity} containing a generic error message and the
     * {@link HttpStatus#INTERNAL_SERVER_ERROR} status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception ex) {
        logger.error("Unexpected error: ", ex);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "An unexpected system error occurred.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500
    }
}