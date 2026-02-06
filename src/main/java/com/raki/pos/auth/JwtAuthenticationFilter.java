package com.raki.pos.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Custom Security Filter that intercepts every HTTP request to validate JWT tokens.
 * <p>
 * This filter sits in the Spring Security chain. It checks the "Authorization" header
 * for a Bearer token. If a valid token is found, it manually sets the user's authentication
 * in the SecurityContext, effectively logging them in for that specific request.
 * </p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    /**
     * Constructor for dependency injection.
     * @param jwtUtil The utility class for parsing and validating tokens.
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * The main filter logic executed for every request.
     *
     * @param request     The incoming HTTP request.
     * @param response    The outgoing HTTP response.
     * @param filterChain The chain of filters that this request must pass through.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null; // Note: In your logic, this variable holds the User's Email.

        // Check if the header contains a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove "Bearer " prefix to get the raw token
            try {
                email = jwtUtil.extractEmail(token); // Extract email (username) from the token
            } catch (Exception e) {
                // Token is invalid, expired, or missing.
                // We log it, but we do NOT stop the request. We let it continue without Authentication.
                // The SecurityConfig will decide later if this request needed to be authenticated or not.
                //System.out.println("JWT Extraction Error: " + e.getMessage());
                logger.warn("JWT extraction error: {}", e.getMessage());
            }
        }

        // Validate the token and set Authentication in Spring Context. If user is already authenticated, we skip this step (if-clause).
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Validate that the token signature matches the user
            if (jwtUtil.validateToken(token, email)) {

                // Create an Authentication Object (The "Badge")
                // We pass 'null' for credentials (password) because they are already verified by the token.
                // We pass an empty list for authorities (roles) because we are not handling roles yet.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>());

                // Add details about the request (IP address, Session ID, etc.) to the badge
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Place the Badge in the Context. The user is now "Logged In".
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 3. Continue the chain. Hand off the request to the next filter or the Controller.
        filterChain.doFilter(request, response);
    }
}