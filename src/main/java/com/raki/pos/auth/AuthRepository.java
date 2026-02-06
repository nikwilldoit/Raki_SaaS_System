package com.raki.pos.auth;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Repository class responsible for database operations related to authentication.
 */
@Repository
public class AuthRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LogManager.getLogger(AuthRepository.class);

    /**
     * Constructor for AuthRepository.
     *
     * @param jdbcTemplate the JdbcTemplate used for executing SQL queries
     */
    public AuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds a user in the database based on the provided email and password.
     *
     * @param email    the email of the user
     * @param password the hashed password of the user
     * @return a Map containing user details (e.g., userId, name, email) if the user is found,
     * or null if no user matches the criteria
     * @throws BadCredentialsException if no user matches the provided email and password
     * @throws DataAccessException if a database access error occurs
     * @throws Exception for any other unexpected errors
     */
    public Map<String, Object> findUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT id, name, email, business_id FROM users WHERE email = ? AND password_hash = ? AND status = 'ACTIVE'";

        try {
            // Executes the query and retrieves the user details as a Map
            Map<String, Object> user = jdbcTemplate.queryForMap(sql, email, password);

            logger.debug("User found: {}", user);
            return user;
        }
        catch (EmptyResultDataAccessException e) {
            // Logs and throws an exception if no user is found
            logger.info("User NOT found for email={}", email);
            throw new BadCredentialsException("Authentication Failed");
        }
        catch (DataAccessException e) {
            // Logs and rethrows database access errors
            logger.error("Database access error for email={}", email, e);
            throw e;
        }
        catch (Exception e) {
            // Logs and rethrows any other unexpected errors
            logger.error("Unexpected error while finding user for email={}", email, e);
            throw e;
        }
    }
}