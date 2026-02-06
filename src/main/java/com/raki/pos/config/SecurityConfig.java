package com.raki.pos.config;

import com.raki.pos.auth.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Configuration class for Spring Security.
 * <p>
 * This class defines the security rules for the application. It configures:
 * 1. CORS (Cross-Origin Resource Sharing) to allow the React frontend to connect.
 * 2. CSRF (Cross-Site Request Forgery) protection, which is disabled for this stateless API.
 * 3. URL access rules (which endpoints are public vs protected).
 * 4. Session management (stateless, using JWTs instead of cookies).
 * 5. The integration of the custom JwtAuthenticationFilter.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor for dependency injection.
     *
     * @param jwtAuthenticationFilter The custom filter that validates JWT tokens.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Defines the security filter chain.
     * <p>
     * This method constructs the "wall" that protects the application.
     * </p>
     *
     * @param http The HttpSecurity object used to configure security settings.
     * @return The built SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF
                .csrf(csrf -> csrf.disable())

                // Configure CORS
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*")); // Allow all headers (like Authorization)
                    config.setAllowCredentials(true);
                    return config;
                }))

                // Defining URL Access Rules
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints: Everyone can access login and status
                        .requestMatchers("/api/login", "/api/status", "/", "/api/test").permitAll()
                        // Private Endpoints: Everything else requires a valid Token
                        .requestMatchers("/api/businesses/**").authenticated()
                        .anyRequest().authenticated()
                )

                // Stateless Session Management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Add Custom Filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}