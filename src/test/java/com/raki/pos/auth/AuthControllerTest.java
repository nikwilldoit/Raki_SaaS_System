package com.raki.pos.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class) // Best Practice: Only load the Controller
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void login_WithValidCredentials_ShouldReturn200() throws Exception {
        // Arrange
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthDTO.LoginResponse response = new AuthDTO.LoginResponse("Test User", "jwt-token-xyz");

        when(authService.login("test@example.com", "password123")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.username").value("Test User"))
                        .andExpect(jsonPath("$.token").value("jwt-token-xyz"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturn401() throws Exception {
        // Arrange
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail("wrong@example.com");
        request.setPassword("wrongpass");

        when(authService.login(any(), any())).thenThrow(new BadCredentialsException("Authentication Failed"));

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Failed"));
    }

    @Test
    void login_WithInvalidEmailFormat_ShouldReturn400() throws Exception {
        // Arrange - Empty email to trigger @Valid
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail(""); // Invalid
        request.setPassword("1234");

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }
}